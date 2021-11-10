package com.beginner.myapplication.features.userdetail

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beginner.myapplication.R
import com.beginner.myapplication.adapter.UserFollSectionsAdapter
import com.beginner.myapplication.adapter.folllist.FollListAdapter
import com.beginner.myapplication.utils.handler.MainHandler
import com.beginner.myapplication.viewmodel.UserFollViewModel
import com.beginner.myapplication.viewmodel.factory.ViewModelFactory

class UserFollTabFragment : Fragment(),
    MainHandler {

    private lateinit var follRecView: RecyclerView
    private lateinit var follRecViewAdapter: FollListAdapter
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var follViewModel: UserFollViewModel
    private lateinit var title: String
    private lateinit var userName: String
    private lateinit var progressBar: ProgressBar
    private lateinit var mainContext: Context

    private var numFollower: Int = 0
    private var numFollowing: Int = 0

    companion object {
        const val TITLE_TAG = "pager_title"
        const val UNAME_TAG = "user_name"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_foll, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            title = arguments?.getString(TITLE_TAG).toString()
            userName = arguments?.getString(UNAME_TAG).toString()
            numFollower =
                arguments?.getInt(view.context.getString(UserFollSectionsAdapter.TITLE[0]))!!
            numFollowing =
                arguments?.getInt(view.context.getString(UserFollSectionsAdapter.TITLE[1]))!!
        }

        follViewModel = ViewModelProvider(
            this,
            ViewModelFactory(
                this,
                this
            )
        ).get(UserFollViewModel::class.java)

        follRecView = view.findViewById(R.id.user_foll_list)
        progressBar = view.findViewById(R.id.foll_progress_bar)
        mLayoutManager = LinearLayoutManager(view.context)
        mainContext = view.context

        follRecView.setHasFixedSize(true)
        follRecView.layoutManager = mLayoutManager

        if (title == view.context.getString(UserFollSectionsAdapter.TITLE[0])) {
            if (follViewModel.savedState.contains(UserFollViewModel.UFOLLOWER_KEY)) {
                follRecViewAdapter = FollListAdapter(follViewModel.loadUserFollower(), this)
                initRVAdapter()
            } else {
                if (!follViewModel.isLoading) {
                    follViewModel.setUsername(userName, title)
                    follViewModel.isLoading = true
                }

                getFollUpdateAdapter(view)

                if (numFollower > 0) {
                    progressBar.visibility = View.VISIBLE
                }

                Log.i("USER_FOLLOWER", "get it")
            }
        } else if (title == view.context.getString(UserFollSectionsAdapter.TITLE[1])) {
            if (follViewModel.savedState.contains(UserFollViewModel.UFOLLOWING_KEY)) {
                follRecViewAdapter = FollListAdapter(follViewModel.loadUserFollowing(), this)
                initRVAdapter()
            } else {
                if (!follViewModel.isLoading) {
                    follViewModel.setUsername(userName, title)
                    follViewModel.isLoading = true
                }

                getFollUpdateAdapter(view)

                if (numFollowing > 0) {
                    progressBar.visibility = View.VISIBLE
                }

                Log.i("USER_FOLLOWING", "get it")
            }
        }
    }

    private fun getFollUpdateAdapter(v: View) {
        if (title == v.context.getString(UserFollSectionsAdapter.TITLE[0])) {
            follViewModel.getFollowerList().observe(this, Observer {
                if (it.size > 0) {
                    follRecViewAdapter = FollListAdapter(it, this)
                    initRVAdapter()
                }
            })
        } else if (title == v.context.getString(UserFollSectionsAdapter.TITLE[1])) {
            follViewModel.getFollowingList().observe(this, Observer {
                if (it.size > 0) {
                    follRecViewAdapter = FollListAdapter(it, this)
                    initRVAdapter()
                }
            })
        }
    }

    private fun initRVAdapter() {
        follRecView.adapter = follRecViewAdapter
        follRecView.adapter?.notifyDataSetChanged()
    }

    override fun stopLoad() {
        progressBar.visibility = View.GONE
    }

    override fun showToast(message: String) {
        (context as MainHandler).showToast(message)
    }

    override fun checkNetworkConn(): Boolean {
        return (context as MainHandler).checkNetworkConn()
    }

    fun getStringContext(resId: Int): String {
        return mainContext.getString(resId)
    }
}