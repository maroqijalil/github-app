package com.beginner.myapplication.viewmodel

import android.content.ContentResolver
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.HandlerThread
import androidx.lifecycle.*
import com.beginner.myapplication.data.db.UserDatabaseTable.UserColumns.Companion.USER_CONTENT_URI
import com.beginner.myapplication.model.UserDetail
import com.beginner.myapplication.utils.UserResource
import com.beginner.myapplication.utils.helper.ObjectHelper
import kotlinx.coroutines.launch

class UserFavViewModel(svdStt: SavedStateHandle, contRes: ContentResolver) :
    ViewModel() {
    private var usersFavList = MutableLiveData<ArrayList<UserResource>>()

    val savedState = svdStt
    private val contentResolver = contRes

    private val scope = viewModelScope
    private var mUsersFavList: MutableSet<String> = mutableSetOf()
    private var _usersFavList = arrayListOf<UserResource>()
    private val thread = HandlerThread("UserDatabaseObserver")
    private val contentObserver: ContentObserver
    val handler: Handler

    companion object {
        const val UFAV_LIST_KEY = "users_fav_list"
    }

    init {
        thread.start()
        handler = Handler(thread.looper)
        contentObserver = object : ContentObserver(handler) {
            override fun onChange(self: Boolean) {
                updateFavList()
            }
        }
        contentResolver.registerContentObserver(USER_CONTENT_URI, true, contentObserver)
    }

    fun getAllUsersFavList() {
        scope.launch {
            val usersCursor = contentResolver.query(USER_CONTENT_URI, null, null, null, null, null)
            val usersList = ObjectHelper.getUsersList(usersCursor)
            usersList.forEach {
                _usersFavList.add(UserResource.addUserToFav(it))
                mUsersFavList.add(it.login!!)
            }
            usersFavList.postValue(_usersFavList)
            saveUsersFavList()
        }
    }

    fun getUsersFavList(): LiveData<ArrayList<UserResource>> {
        return usersFavList
    }

    private fun dropUserFromFavByString(name: ArrayList<String>) {
        scope.launch {

            name.forEach { s ->
                var index = 0
                var change = false

                _usersFavList.forEachIndexed Lit@{ j, second ->
                    if (second.data.login == s) {
                        _usersFavList[j].favState = false
                        if (mUsersFavList.contains(s)) {
                            mUsersFavList.remove(s)
                        }
                        change = true
                        index = j
                        return@Lit
                    }
                }

                if (change) {
                    _usersFavList.removeAt(index)
                }
            }

            usersFavList.postValue(_usersFavList)
            saveUsersFavList()
        }
    }

    fun updateFavList() {
        scope.launch {
            val usersCursor = contentResolver.query(USER_CONTENT_URI, null, null, null, null, null)
            val usersListTemp = ObjectHelper.getUsersList(usersCursor)
            val userFavListTemp = mutableSetOf<String>()
            val usersTemp = arrayListOf<String>()

            userFavListTemp.addAll(mUsersFavList)
            usersListTemp.forEach { user ->
                if (userFavListTemp.contains(user.login)) {
                    userFavListTemp.remove(user.login)
                } else {
                    usersTemp.add(user.login!!)
                }
            }

            if (userFavListTemp.size > 0) {
                usersTemp.addAll(userFavListTemp)
                dropUserFromFavByString(usersTemp)
            }
        }
    }

    fun findUserForFix(username: String): Boolean {
        if (mUsersFavList.contains(username)) {
            return true
        }

        return false
    }

    private fun saveUsersFavList() {
        if (savedState.contains(UFAV_LIST_KEY)) {
            savedState.remove<ArrayList<UserResource>>(UFAV_LIST_KEY)
            savedState.set(UFAV_LIST_KEY, _usersFavList)
        } else {
            savedState.set(UFAV_LIST_KEY, _usersFavList)
        }
    }

    fun loadUsersFavList(): ArrayList<UserResource> {
        return savedState.get<ArrayList<UserResource>>(UFAV_LIST_KEY) ?: arrayListOf()
    }

    fun dropUserFromFav(user: UserDetail): Int {
        val uri = Uri.parse("$USER_CONTENT_URI/${user.login}")
        return contentResolver.delete(uri, null, null)
    }
}