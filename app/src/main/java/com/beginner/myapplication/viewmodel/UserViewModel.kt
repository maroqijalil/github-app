package com.beginner.myapplication.viewmodel

import android.content.ContentResolver
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import androidx.lifecycle.*
import com.beginner.myapplication.R
import com.beginner.myapplication.data.db.UserDatabaseTable.UserColumns.Companion.USER_CONTENT_URI
import com.beginner.myapplication.model.SearchUsersList
import com.beginner.myapplication.model.UserDetail
import com.beginner.myapplication.utils.helper.ObjectHelper
import com.beginner.myapplication.utils.UserResource
import com.beginner.myapplication.utils.handler.MainHandler
import com.beginner.myapplication.utils.network.GitHubEndpoint
import com.beginner.myapplication.utils.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.net.SocketTimeoutException
import javax.net.ssl.SSLException

class UserViewModel(ctx: Context, svd: SavedStateHandle, contRes: ContentResolver) : ViewModel() {

    private var userSearch = MutableLiveData<SearchUsersList>()
    var userData = MutableLiveData<ArrayList<UserResource>>()

    val context = ctx
    val savedState = svd
    private val contentResolver = contRes

    private lateinit var gitHubClient: GitHubEndpoint
    private lateinit var job: Job

    private var userFavList: MutableSet<String> = mutableSetOf()
    private var scope = viewModelScope
    private var userUsername = ""
    private val noNetworkMsg = context.getString(R.string.no_net_connection)
    private val thread = HandlerThread("UserDatabaseObserver")
    private val contentObserver: ContentObserver
    val handler: Handler

    private var _userSearch: SearchUsersList = SearchUsersList()
    var _userData: ArrayList<UserResource> = arrayListOf()

    companion object {
        const val UDETAIL_KEY = "user_detail_key"
    }

    init {
        try {
            gitHubClient = RetrofitClient.gitHubClient
        } catch (e: SocketTimeoutException) {
            e.printStackTrace()
            (context as MainHandler).showToast(e.message.toString())
        } catch (e: Exception) {
            e.printStackTrace()
            (context as MainHandler).showToast(e.message.toString())
        }

        thread.start()
        handler = Handler(thread.looper)
        contentObserver = object : ContentObserver(handler) {
            override fun onChange(self: Boolean) {
                updateFavList()
            }
        }
        contentResolver.registerContentObserver(USER_CONTENT_URI, true, contentObserver)
    }

    fun networkErrorHandler(err: Int, resMsg: String?) {
        val errMsg = when (err) {
            401 -> "$err : Bad Request"
            403 -> "$err : Forbidden"
            404 -> "$err : Not Found"
            else -> "$err : $resMsg"
        }
        (context as MainHandler).showToast(errMsg)
    }

    fun setUserSearch(username: String) {
        userUsername = username

        checkNClearComponents(0)
        job = scope.launch {
            try {
                getSearchItem(username, false)
            } catch (e: Exception) {
                e.printStackTrace()
                userSearch.postValue(SearchUsersList(false, arrayListOf(), 0))
            }
        }
    }

    fun submitUserSearch(username: String) {
        userUsername = username

        checkNClearComponents(1)
        job = scope.launch {
            if ((context as MainHandler).checkNetworkConn()) {
                getSearchItem(username, true)
            } else {
                (context as MainHandler).showToast(noNetworkMsg)
                (context as MainHandler).stopLoad()
            }
        }
    }

    fun getUserSearch(): LiveData<SearchUsersList> {
        if (_userSearch.items.isEmpty()) {
            setUserSearch(userUsername)
        }

        return userSearch
    }

    fun getUserDetail(): LiveData<ArrayList<UserResource>> {
        if (_userData.size <= 0) {
            submitUserSearch(userUsername)
        }

        return userData
    }

    private fun checkNClearComponents(type: Int) {
        if (_userData.isNotEmpty() && type == 1) {
            _userData.clear()
        }

        if (this::job.isInitialized) {
            if (job.isActive)
                job.cancel()
        }
    }

    fun setUserToFav(user: UserDetail) {
        scope.launch {
            val value = ObjectHelper.getContentValue(user)
            contentResolver.insert(USER_CONTENT_URI, value)
        }
    }

    fun dropUserFromFav(user: UserDetail): Int {
        val uri = Uri.parse("$USER_CONTENT_URI/${user.login}")
        return contentResolver.delete(uri, null, null)
    }

    private fun dropUserFromFavByString(name: ArrayList<String>) {
        scope.launch {
            name.forEach { s ->
                _userData.forEachIndexed Lit@{ j, second ->
                    if (second.data.login == s) {
                        _userData[j].favState = false
                        if (userFavList.contains(s)) {
                            userFavList.remove(s)
                        }
                        return@Lit
                    }
                }
            }

            userData.postValue(_userData)
            saveUserDetail()
        }
    }

    private fun insertUserToFavByString(name: ArrayList<String>) {
        scope.launch {
            name.forEach { s ->
                _userData.forEachIndexed Lit@{ j, second ->
                    if (second.data.login == s) {
                        _userData[j].favState = true
                        if (!userFavList.contains(s)) {
                            userFavList.add(s)
                        }
                        return@Lit
                    }
                }
            }

            userData.postValue(_userData)
            saveUserDetail()
        }
    }

    private fun checkUsersList() {
        val usersCursor = contentResolver.query(USER_CONTENT_URI, null, null, null, null, null)
        val usersListTemp = ObjectHelper.getUsersList(usersCursor)

        usersListTemp.forEachIndexed { _, first ->
            _userData.forEachIndexed Lit@{ j, second ->
                if (second.data.login == first.login) {
                    _userData[j].favState = true
                    return@Lit
                }
            }
        }

        userData.postValue(_userData)
        saveUserDetail()
    }

    fun updateFavList() {
        scope.launch {
            val usersCursor = contentResolver.query(USER_CONTENT_URI, null, null, null, null, null)
            val usersListTemp = ObjectHelper.getUsersList(usersCursor)
            val userFavListTemp = mutableSetOf<String>()
            val usersTemp = arrayListOf<String>()

            userFavListTemp.addAll(userFavList)
            usersListTemp.forEach { user ->
                if (userFavListTemp.contains(user.login)) {
                    userFavListTemp.remove(user.login)
                } else {
                    usersTemp.add(user.login!!)
                }
            }

            if (usersTemp.size > 0) {
                insertUserToFavByString(usersTemp)
            }
            if (userFavListTemp.size > 0) {
                usersTemp.clear()
                usersTemp.addAll(userFavListTemp)
                dropUserFromFavByString(usersTemp)
            }
        }
    }

    fun findUserForFix(username: String): Boolean {
        if (userFavList.contains(username)) {
            return true
        }

        return false
    }

    fun insertUserFromMain(usersList: ArrayList<UserResource>) {
        scope.launch {
            _userData.addAll(usersList)
            checkUsersList()
        }
    }

    private fun saveUserDetail() {
        if (savedState.contains(UDETAIL_KEY)) {
            savedState.remove<ArrayList<UserResource>>(UDETAIL_KEY)
            savedState.set(UDETAIL_KEY, _userData)
        } else {
            savedState.set(UDETAIL_KEY, _userData)
        }
    }

    fun loadUserDetail(): ArrayList<UserResource> {
        return savedState.get<ArrayList<UserResource>>(UDETAIL_KEY) ?: arrayListOf()
    }

    private fun getGitHubDetail(username: String) {
        val response: Call<UserDetail>

        try {
            response = RetrofitClient.gitHubClient.getDetail(username)

            response.enqueue(object : Callback<UserDetail> {
                override fun onFailure(call: Call<UserDetail>, t: Throwable) {
                    Log.e("REQUEST", "Ooops: Something else went wrong")
                }

                override fun onResponse(call: Call<UserDetail>, response: Response<UserDetail>) {
                    if (response.isSuccessful) {
                        _userData.add(UserResource.addUser(response.body()!!))
                        scope.launch { checkUsersList() }
                    } else {
                        networkErrorHandler(response.code(), response.message())
                    }
                }
            })
        } catch (e: SocketTimeoutException) {
            e.printStackTrace()
            (context as MainHandler).stopLoad()
            (context as MainHandler).showToast(context.getString(R.string.con_timeout))
        } catch (e: SSLException) {
            e.printStackTrace()
            (context as MainHandler).stopLoad()
            (context as MainHandler).showToast(context.getString(R.string.con_abort))
        } catch (e: Exception) {
            e.printStackTrace()
            (context as MainHandler).stopLoad()
            (context as MainHandler).showToast(e.message.toString())
        }
    }

    private suspend fun getSearchItem(username: String, submit: Boolean) {
        val mUserSearch: SearchUsersList
        val response: Response<SearchUsersList>

        try {

            response = RetrofitClient.gitHubClient.getSearch(username)
            try {
                if (response.isSuccessful) {
                    if (response.body()?.total_count == 0) {
                        (context as MainHandler).stopLoad()
                    }

                    if (submit) {
                        mUserSearch = response.body()!!

                        mUserSearch.items.forEach {
                            getGitHubDetail(it.login!!)
                        }
                    } else {
                        _userSearch = response.body()!!
                        userSearch.postValue(response.body())
                    }
                } else {
                    networkErrorHandler(response.code(), response.message())
                    (context as MainHandler).stopLoad()
                }
            } catch (e: HttpException) {
                (context as MainHandler).stopLoad()
                Log.e("REQUEST", "Exception ${e.message}")
            } catch (e: Throwable) {
                (context as MainHandler).stopLoad()
                Log.e("REQUEST", "Ooops: Something else went wrong")
            }
        } catch (e: SocketTimeoutException) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                (context as MainHandler).stopLoad()
                (context as MainHandler).showToast(context.getString(R.string.con_timeout))
            }
        } catch (e: SSLException) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                (context as MainHandler).stopLoad()
                (context as MainHandler).showToast(context.getString(R.string.con_abort))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                (context as MainHandler).stopLoad()
                (context as MainHandler).showToast(e.message.toString())
            }
        }
    }
}