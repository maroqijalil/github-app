package com.beginner.myapplication.adapter

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.beginner.myapplication.features.userdetail.NoNetFragment
import com.beginner.myapplication.R
import com.beginner.myapplication.features.userdetail.UserFollTabFragment

class UserFollSectionsAdapter(
    ctx: Context,
    fragmentManager: FragmentManager,
    uname: String,
    private val follower: Int,
    private val following: Int,
    isConnected: Boolean
) :
    FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    val username = uname
    val context = ctx
    private val isNetConnected = isConnected

    companion object {
        val TITLE = intArrayOf(
            R.string.follower_title,
            R.string.following_title
        )
    }

    override fun getItem(position: Int): Fragment {
        return if (isNetConnected) {
            setFragment(context.getString(TITLE[position]))
        } else {
            NoNetFragment()
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.getString(TITLE[position])
    }

    private fun setFragment(title: String): UserFollTabFragment {
        val follFragment =
            UserFollTabFragment()
        val bundle = Bundle()

        bundle.putString(UserFollTabFragment.TITLE_TAG, title)
        bundle.putString(UserFollTabFragment.UNAME_TAG, username)
        bundle.putInt(context.getString(TITLE[0]), follower)
        bundle.putInt(context.getString(TITLE[1]), following)
        follFragment.arguments = bundle

        return follFragment
    }
}