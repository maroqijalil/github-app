@file:Suppress("DEPRECATION")

package com.beginner.myapplication.features.userdetail

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.beginner.myapplication.R
import com.beginner.myapplication.adapter.UserFollSectionsAdapter
import com.beginner.myapplication.model.UserDetail
import com.beginner.myapplication.model.UserDrawable
import com.beginner.myapplication.utils.handler.MainHandler
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_user_detail.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class UserDetailActivity : AppCompatActivity(),
    MainHandler {

    private lateinit var sectionsAdapter: UserFollSectionsAdapter
    private lateinit var userFollPager: ViewPager
    private lateinit var userDetail: UserDetail

    private var isNetConnected = false

    companion object {
        const val PARCEL_TAG = "parcel_detail"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail)

        isNetConnected = checkNetworkConn()

        userFollPager = findViewById(R.id.user_foll_pager)

        userDetail = intent.getParcelableExtra(PARCEL_TAG)

        fillTheContent()

        sectionsAdapter =
            UserFollSectionsAdapter(
                this,
                supportFragmentManager,
                userDetail.login!!,
                userDetail.followers,
                userDetail.following,
                isNetConnected
            )
        userFollPager.adapter = sectionsAdapter
        tabs_pager.setupWithViewPager(userFollPager)
    }

    override fun onStart() {
        super.onStart()
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancelAll()
    }

    @SuppressLint("SetTextI18n")
    fun fillTheContent() {
        if (UserDrawable.drawable[userDetail.avatar_url] != null) {
            Picasso.get()
                .load(UserDrawable.drawable[userDetail.avatar_url]!!)
                .into(user_detail_avatar)
        } else {
            Picasso.get()
                .load(userDetail.avatar_url)
                .into(user_detail_avatar)
        }

        user_detail_name.text = userDetail.name
        user_detail_username.text = userDetail.login
        user_detail_location.text = userDetail.location
        user_detail_following.text = userDetail.following.toString() + " " + this.getString(
            R.string.following
        )
        user_detail_company.text = userDetail.company

        if (userDetail.followers > 1) {
            user_detail_followers.text = userDetail.followers.toString() + " " + this.getString(
                R.string.followers
            )
        } else if (userDetail.followers <= 1) {
            user_detail_followers.text = userDetail.followers.toString() + " " + this.getString(
                R.string.follower
            )
        }
        if (userDetail.public_repos > 1) {
            user_detail_repositories.text =
                userDetail.public_repos.toString() + " " + this.getString(
                    R.string.repositories
                )
        } else if (userDetail.public_repos <= 1) {
            user_detail_repositories.text =
                userDetail.public_repos.toString() + " " + this.getString(
                    R.string.repository
                )
        }
    }

    @Suppress("DEPRECATION")
    override fun checkNetworkConn(): Boolean {
        val connectionManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectionManager.activeNetworkInfo
        return networkInfo?.isConnected == true
    }

    override fun stopLoad() {
    }

    override fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}