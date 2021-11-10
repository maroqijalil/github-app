@file:Suppress("DEPRECATION")

package com.beginner.myapplication.features.main

import android.app.AlertDialog
import android.app.NotificationManager
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.MatrixCursor
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.provider.BaseColumns
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.cursoradapter.widget.CursorAdapter
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beginner.myapplication.R
import com.beginner.myapplication.features.SettingActivity
import com.beginner.myapplication.adapter.ListDecoration
import com.beginner.myapplication.adapter.userlist.UsersListAdapter
import com.beginner.myapplication.features.FavUserActivity
import com.beginner.myapplication.features.userdetail.UserDetailActivity
import com.beginner.myapplication.model.SearchUsersList
import com.beginner.myapplication.model.UserDetail
import com.beginner.myapplication.model.UsersList
import com.beginner.myapplication.pref.AppPreferences
import com.beginner.myapplication.utils.JsonProcess
import com.beginner.myapplication.utils.UserResource
import com.beginner.myapplication.utils.handler.MainHandler
import com.beginner.myapplication.utils.handler.NavigationHandler
import com.beginner.myapplication.utils.helper.DBHelper
import com.beginner.myapplication.utils.helper.NotificationHelper
import com.beginner.myapplication.viewmodel.UserViewModel
import com.beginner.myapplication.viewmodel.factory.ViewModelFactoryWithContRes
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(),
    MainHandler, NavigationHandler,
    DBHelper {

    private lateinit var userRecView: RecyclerView
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var sCursorAdapter: SimpleCursorAdapter
    private lateinit var alertDialog: AlertDialog.Builder
    private lateinit var favButton: FloatingActionButton
    private lateinit var recViewAdapter: UsersListAdapter
    private lateinit var notification: NotificationHelper
    private lateinit var appPreferences: AppPreferences

    lateinit var userDetailObserver: Observer<ArrayList<UserResource>>
    lateinit var userSearchObserver: Observer<SearchUsersList>
    lateinit var userViewModel: UserViewModel

    private var usersSearchSuggestion: ArrayList<String> = arrayListOf()
    private var usersListDetail: ArrayList<UserResource> = arrayListOf()
    var lastSearch: String = ""

    private val exGithubUsers: UsersList
        get() {
            val gson = Gson()
            val jsonStringFromFile: String? =
                JsonProcess.getJsonString(this, "githubuser.json")
            val usersListType = object : TypeToken<UsersList>() {}.type
            return gson.fromJson(jsonStringFromFile, usersListType)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getCurrentUsersList()
        setupItemView()
        setupObserver()
        setupView()
    }

    override fun onStart() {
        super.onStart()
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancelAll()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        val searchBar = menu?.findItem(R.id.search_bar)

        if (searchBar != null) {
            val searchView = searchBar.actionView as SearchView
            val etSearch: EditText =
                searchView.findViewById(androidx.appcompat.R.id.search_src_text)
            val dest = arrayOf(SearchManager.SUGGEST_COLUMN_TEXT_1)
            val source = intArrayOf(R.id.search_item)

            sCursorAdapter = SimpleCursorAdapter(
                this,
                R.layout.item_user_search,
                null,
                dest,
                source,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
            )

            searchView.findViewById<AutoCompleteTextView>(R.id.search_src_text).threshold = 1
            etSearch.hint = getString(R.string.search_hint)
            searchView.suggestionsAdapter = sCursorAdapter

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    lastSearch = query!!
                    main_progress_bar.visibility = View.VISIBLE

                    userViewModel.submitUserSearch(lastSearch)
                    userViewModel.getUserDetail().observe(this@MainActivity, userDetailObserver)

                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText?.isNotEmpty()!!) {
                        lastSearch = newText

                        userViewModel.setUserSearch(lastSearch)
                        userViewModel.getUserSearch().observe(this@MainActivity, userSearchObserver)
                    }
                    return true
                }
            })

            searchView.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
                override fun onSuggestionSelect(position: Int): Boolean {
                    return false
                }

                override fun onSuggestionClick(position: Int): Boolean {
                    val item = searchView.suggestionsAdapter.getItem(position) as Cursor
                    val selectedUser =
                        item.getString(item.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1))
                    searchView.setQuery(selectedUser, true)

                    return true
                }
            })
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.my_profile -> {
                startActivity(Intent(this@MainActivity, AboutProfileActivity::class.java))
                true
            }
            R.id.setting_btn -> {
                startActivity(Intent(this@MainActivity, SettingActivity::class.java))
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    @Suppress("DEPRECATION")
    override fun checkNetworkConn(): Boolean {
        val connectionManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectionManager.activeNetworkInfo
        return networkInfo?.isConnected == true
    }

    private fun updateSuggestionCursor() {
        val cursor = MatrixCursor(arrayOf(BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1))

        lastSearch.let {
            usersSearchSuggestion.forEachIndexed { index, s ->
                if (s.contains(lastSearch, true))
                    cursor.addRow(arrayOf(index, s))
            }
        }
        sCursorAdapter.changeCursor(cursor)
    }

    private fun getCurrentUsersList() {
        val usersList = exGithubUsers

        usersList.users.forEach {
            val user = it
            val newUser: UserDetail

            newUser = UserDetail(
                name = user.name,
                login = user.username,
                public_repos = user.repository,
                followers = user.follower,
                following = user.following,
                company = user.company,
                location = user.location,
                avatar_url = user.avatar
            )

            usersSearchSuggestion.add(newUser.name!!)
            usersListDetail.add(UserResource.addUser(newUser))
        }
    }

    private fun setupItemView() {
        userViewModel = ViewModelProvider(this, ViewModelFactoryWithContRes(this, this, contentResolver))
            .get(UserViewModel::class.java)
        alertDialog = AlertDialog.Builder(this, R.style.Theme_AppCompat_DayNight_Dialog_Alert)
        userRecView = findViewById(R.id.users_list)
        favButton = findViewById(R.id.fav_button)
        mLayoutManager = LinearLayoutManager(this)
        notification = NotificationHelper(this)
        appPreferences = AppPreferences(this)
    }

    private fun setupObserver() {
        userDetailObserver = Observer {
            if (it.size > 0) {
                if (usersListDetail.size > 0) {
                    usersListDetail.clear()
                }

                usersListDetail.addAll(it)
                (userRecView.adapter as UsersListAdapter).changeUsersList(usersListDetail)
            }
        }

        userSearchObserver = Observer { it ->
            if (it.items.isNotEmpty()) {
                usersSearchSuggestion.clear()

                it.items.forEach {
                    usersSearchSuggestion.add(it.login!!)
                }

                updateSuggestionCursor()

            } else if (it.total_count == 0) {
                usersSearchSuggestion.clear()
                updateSuggestionCursor()
            }
        }
    }

    private fun setupView() {
        if (userViewModel.savedState.contains(UserViewModel.UDETAIL_KEY)) {
            recViewAdapter = UsersListAdapter(userViewModel.loadUserDetail())
        } else {
            userViewModel.insertUserFromMain(usersListDetail)

            userViewModel.getUserDetail().observe(this, userDetailObserver)
            recViewAdapter = UsersListAdapter(usersListDetail)
        }

        userRecView.setHasFixedSize(true)
        userRecView.layoutManager = mLayoutManager
        userRecView.adapter = recViewAdapter
        userRecView.addItemDecoration(ListDecoration(16))

        favButton.setOnClickListener {
            startActivity(Intent(this, FavUserActivity::class.java))
        }

        if (appPreferences.getNotifPref()) {
            appPreferences.setNotifPref(true)
            if (!appPreferences.getNotifState()) {
                appPreferences.setNotifState(true)
                GlobalScope.launch(Dispatchers.Default) { notification.turnOnNotification() }
            }
        }
    }

    override fun stopLoad() {
        runOnUiThread {
            main_progress_bar.visibility = View.GONE
        }
    }

    override fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun navigateTo(user: UserDetail) {
        val intent = Intent(this, UserDetailActivity::class.java)
        intent.putExtra(UserDetailActivity.PARCEL_TAG, user)
        startActivity(intent)
    }

    override fun insertUser(user: UserDetail) {
        userViewModel.setUserToFav(user)
    }

    override fun showAlert(title: String, msg: String, user: UserDetail, pos: Int) {
        alertDialog.setTitle(title)
            .setMessage(msg)
            .setPositiveButton(getString(R.string.ask_yes)) { _, _ ->
                val res = userViewModel.dropUserFromFav(user)
                if (res > 0) {
                    showToast(user.login + " " + getString(R.string.del_title2))
                    (userRecView.adapter as UsersListAdapter).deleteUserFromFav(pos)
                } else {
                    userViewModel.updateFavList()
                    if (userViewModel.findUserForFix(user.login!!)) {
                        showToast(getString(R.string.del_fail) + " " + user.login)
                    } else {
                        showToast(user.login + " " + getString(R.string.del_title2))
                        (userRecView.adapter as UsersListAdapter).deleteUserFromFav(pos)
                    }
                }
            }
            .setNegativeButton(getString(R.string.ask_no)) { dialog, _ -> dialog.cancel() }
            .create().show()
    }
}