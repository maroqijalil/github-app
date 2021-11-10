@file:Suppress("DEPRECATION")

package com.beginner.myapplication.features

import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beginner.myapplication.R
import com.beginner.myapplication.adapter.ListDecoration
import com.beginner.myapplication.adapter.userlist.UsersListAdapter
import com.beginner.myapplication.features.userdetail.UserDetailActivity
import com.beginner.myapplication.model.UserDetail
import com.beginner.myapplication.utils.UserResource
import com.beginner.myapplication.utils.handler.MainHandler
import com.beginner.myapplication.utils.handler.NavigationHandler
import com.beginner.myapplication.utils.helper.DBHelper
import com.beginner.myapplication.viewmodel.UserFavViewModel
import com.beginner.myapplication.viewmodel.factory.ViewModelFactoryWithContRes

@Suppress("IMPLICIT_CAST_TO_ANY", "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class FavUserActivity : AppCompatActivity(),
    MainHandler, NavigationHandler,
    DBHelper {

    private lateinit var usersFavRecView: RecyclerView
    private lateinit var mLinearLayoutManager: LinearLayoutManager
    private lateinit var usersFavViewModel: UserFavViewModel
    private lateinit var usersFavListAdapter: UsersListAdapter
    private lateinit var alertDialog: AlertDialog.Builder

    private var usersFavList = arrayListOf<UserResource>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fav_user)

        setupItemView()
        setupView()
    }

    override fun onStart() {
        super.onStart()
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancelAll()
    }

    private fun setupItemView() {
        usersFavRecView = findViewById(R.id.fav_users_list)
        mLinearLayoutManager = LinearLayoutManager(this)
        alertDialog = AlertDialog.Builder(this, R.style.Theme_AppCompat_DayNight_Dialog_Alert)
        usersFavViewModel =
            ViewModelProvider(this, ViewModelFactoryWithContRes(this, this, contentResolver)).get(
                UserFavViewModel::class.java
            )

        if (usersFavViewModel.savedState.contains(UserFavViewModel.UFAV_LIST_KEY)) {
            usersFavListAdapter = UsersListAdapter(usersFavViewModel.loadUsersFavList())
        } else {
            usersFavListAdapter = UsersListAdapter(usersFavList)
            usersFavViewModel.getAllUsersFavList()
        }
    }

    override fun stopLoad() {
    }

    private fun setupView() {
        if (!checkNetworkConn()) {
            showToast(getString(R.string.no_net_connection))
        }

        usersFavRecView.apply {
            setHasFixedSize(true)
            layoutManager = mLinearLayoutManager
            adapter = usersFavListAdapter
            addItemDecoration(ListDecoration(16))
        }
        usersFavViewModel.getUsersFavList().observe(this, Observer {
            if (it.size > 0) {
                if (usersFavList.size > 0) {
                    usersFavList.clear()
                }

                usersFavList.addAll(it)
                (usersFavRecView.adapter as UsersListAdapter).changeUsersList(usersFavList)
            }
        })
    }

    override fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    @Suppress("DEPRECATION")
    override fun checkNetworkConn(): Boolean {
        val connectionManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectionManager.activeNetworkInfo
        return networkInfo?.isConnected == true
    }

    override fun navigateTo(user: UserDetail) {
        val intent = Intent(this, UserDetailActivity::class.java)
        intent.putExtra(UserDetailActivity.PARCEL_TAG, user)
        startActivity(intent)
    }

    override fun insertUser(user: UserDetail) {
    }

    override fun showAlert(title: String, msg: String, user: UserDetail, pos: Int) {
        alertDialog.setTitle(title)
            .setMessage(msg)
            .setPositiveButton(getString(R.string.ask_yes)) { _, _ ->
                val res = usersFavViewModel.dropUserFromFav(user)
                if (res > 0) {
                    (usersFavRecView.adapter as UsersListAdapter).deleteUser(pos)
                    showToast(user.login + " " + getString(R.string.del_title2))
                } else {
                    usersFavViewModel.updateFavList()
                    if (usersFavViewModel.findUserForFix(user.login!!)) {
                        showToast(getString(R.string.del_fail) + " " + user.login)
                    } else {
                        showToast(user.login + " " + getString(R.string.del_title2))
                    }
                }
            }
            .setNegativeButton(getString(R.string.ask_no)) { dialog, _ -> dialog.cancel() }
            .create().show()
    }
}