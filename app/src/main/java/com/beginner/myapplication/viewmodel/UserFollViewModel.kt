package com.beginner.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.beginner.myapplication.R
import com.beginner.myapplication.adapter.UserFollSectionsAdapter
import com.beginner.myapplication.features.userdetail.UserFollTabFragment
import com.beginner.myapplication.model.UserDetail
import com.beginner.myapplication.model.UserFoll
import com.beginner.myapplication.utils.handler.MainHandler
import com.beginner.myapplication.utils.network.GitHubEndpoint
import com.beginner.myapplication.utils.network.RetrofitClient
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.net.SocketTimeoutException
import javax.net.ssl.SSLException

class UserFollViewModel(ctx: UserFollTabFragment, svdStt: SavedStateHandle) : ViewModel() {

    var follower = MutableLiveData<ArrayList<UserDetail?>>()
    var following = MutableLiveData<ArrayList<UserDetail?>>()

    private lateinit var followerTemp: UserFoll
    private lateinit var followingTemp: UserFoll
    private lateinit var gitHubClient: GitHubEndpoint

    val context = ctx
    val savedState = svdStt

    var _follower = arrayListOf<UserDetail?>()
    var _following = arrayListOf<UserDetail?>()
    private var scope = CoroutineScope(Job() + Dispatchers.Default)
    var isLoading = false

    companion object {
        const val UFOLLOWER_KEY = "user_follower_key"
        const val UFOLLOWING_KEY = "user_following_key"
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

    fun setUsername(username: String, type: String) {
        if (type == context.getStringContext(UserFollSectionsAdapter.TITLE[0])) {
            scope.launch {
                getFollowerItem(username)
            }
        } else if (type == context.getStringContext(UserFollSectionsAdapter.TITLE[1])) {
            scope.launch {
                getFollowingItem(username)
            }
        }
    }

    fun getFollowerList(): LiveData<ArrayList<UserDetail?>> {
        return follower
    }

    fun getFollowingList(): LiveData<ArrayList<UserDetail?>> {
        return following
    }

    fun saveUserFollower() {
        if (savedState.contains(UFOLLOWER_KEY)) {
            savedState.remove<ArrayList<UserDetail>>(UFOLLOWER_KEY)
            savedState.set(UFOLLOWER_KEY, _follower)
        } else {
            savedState.set(UFOLLOWER_KEY, _follower)
        }
    }

    fun loadUserFollower(): ArrayList<UserDetail?> {
        return savedState.get<ArrayList<UserDetail?>>(UFOLLOWER_KEY) ?: arrayListOf()
    }

    fun saveUserFollowing() {
        if (savedState.contains(UFOLLOWING_KEY)) {
            savedState.remove<ArrayList<UserDetail>>(UFOLLOWING_KEY)
            savedState.set(UFOLLOWING_KEY, _following)
        } else {
            savedState.set(UFOLLOWING_KEY, _following)
        }
    }

    fun loadUserFollowing(): ArrayList<UserDetail?> {
        return savedState.get<ArrayList<UserDetail?>>(UFOLLOWING_KEY) ?: arrayListOf()
    }

    private fun getGitHubDetail(username: String, type: Int) {
        val response: Call<UserDetail>

        try {
            response = gitHubClient.getDetail(username)

            response.enqueue(object : Callback<UserDetail> {
                override fun onFailure(call: Call<UserDetail>, t: Throwable) {
                    Log.e("REQUEST", "Ooops: Something else went wrong")
                }

                override fun onResponse(call: Call<UserDetail>, response: Response<UserDetail>) {
                    if (response.isSuccessful) {
                        if (type == 1) {
                            _follower.add(response.body()!!)
                            follower.postValue(_follower)
                            saveUserFollower()
                        } else if (type == 2) {
                            _following.add(response.body()!!)
                            following.postValue(_following)
                            saveUserFollowing()
                        }
                    } else {
                        networkErrorHandler(response.code(), response.message())
                    }
                }
            })
        } catch (e: SocketTimeoutException) {
            e.printStackTrace()
            (context as MainHandler).stopLoad()
            (context as MainHandler).showToast(context.getStringContext(R.string.con_timeout))
        } catch (e: SSLException) {
            e.printStackTrace()
            (context as MainHandler).stopLoad()
            (context as MainHandler).showToast(context.getStringContext(R.string.con_abort))
        } catch (e: Exception) {
            e.printStackTrace()
            (context as MainHandler).stopLoad()
            (context as MainHandler).showToast(e.message.toString())
        }
    }

    private suspend fun getFollowerItem(username: String) {
        val response: Response<UserFoll>

        try {
            response = gitHubClient.getFollowers(username)

            try {
                if (response.isSuccessful) {
                    if (response.body()?.size!! < 1) {
                        withContext(Dispatchers.Main) {
                            (context as MainHandler).stopLoad()
                        }
                    }

                    followerTemp = response.body()!!

                    followerTemp.forEach {
                        getGitHubDetail(it.login!!, 1)
                    }
                } else {
                    networkErrorHandler(response.code(), response.message())

                    withContext(Dispatchers.Main) {
                        (context as MainHandler).stopLoad()
                    }
                }
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    (context as MainHandler).stopLoad()
                }

                Log.e("REQUEST", "Exception ${e.message}")
            } catch (e: Throwable) {
                withContext(Dispatchers.Main) {
                    (context as MainHandler).stopLoad()
                }

                Log.e("REQUEST", "Ooops: Something else went wrong")
            }
        } catch (e: SocketTimeoutException) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                (context as MainHandler).stopLoad()
                (context as MainHandler).showToast(context.getStringContext(R.string.con_timeout))
            }
        } catch (e: SSLException) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                (context as MainHandler).stopLoad()
                (context as MainHandler).showToast(context.getStringContext(R.string.con_abort))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                (context as MainHandler).stopLoad()
                (context as MainHandler).showToast(e.message.toString())
            }
        }
    }

    private suspend fun getFollowingItem(username: String) {
        val response: Response<UserFoll>

        try {
            response = gitHubClient.getFollowing(username)

            try {
                if (response.isSuccessful) {
                    if (response.body()?.size!! < 1) {
                        withContext(Dispatchers.Main) {
                            (context as MainHandler).stopLoad()
                        }
                    }

                    followingTemp = response.body()!!

                    followingTemp.forEach {
                        getGitHubDetail(it.login!!, 2)
                    }
                } else {
                    networkErrorHandler(response.code(), response.message())

                    withContext(Dispatchers.Main) {
                        (context as MainHandler).stopLoad()
                    }
                }
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    (context as MainHandler).stopLoad()
                }

                Log.e("REQUEST", "Exception ${e.message}")
            } catch (e: Throwable) {
                withContext(Dispatchers.Main) {
                    (context as MainHandler).stopLoad()
                }

                Log.e("REQUEST", "Ooops: Something else went wrong")
            }
        } catch (e: SocketTimeoutException) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                (context as MainHandler).stopLoad()
                (context as MainHandler).showToast(context.getStringContext(R.string.con_timeout))
            }
        } catch (e: SSLException) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                (context as MainHandler).stopLoad()
                (context as MainHandler).showToast(context.getStringContext(R.string.con_abort))
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