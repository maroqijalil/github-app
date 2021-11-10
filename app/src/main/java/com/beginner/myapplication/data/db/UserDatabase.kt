package com.beginner.myapplication.data.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.beginner.myapplication.data.db.UserDatabaseTable.UserColumns.Companion.AVATAR_URL
import com.beginner.myapplication.data.db.UserDatabaseTable.UserColumns.Companion.COMPANY
import com.beginner.myapplication.data.db.UserDatabaseTable.UserColumns.Companion.EMAIL
import com.beginner.myapplication.data.db.UserDatabaseTable.UserColumns.Companion.FOLLOWERS
import com.beginner.myapplication.data.db.UserDatabaseTable.UserColumns.Companion.FOLLOWERS_URL
import com.beginner.myapplication.data.db.UserDatabaseTable.UserColumns.Companion.FOLLOWING
import com.beginner.myapplication.data.db.UserDatabaseTable.UserColumns.Companion.FOLLOWING_URL
import com.beginner.myapplication.data.db.UserDatabaseTable.UserColumns.Companion.LOCATION
import com.beginner.myapplication.data.db.UserDatabaseTable.UserColumns.Companion.LOGIN
import com.beginner.myapplication.data.db.UserDatabaseTable.UserColumns.Companion.NAME
import com.beginner.myapplication.data.db.UserDatabaseTable.UserColumns.Companion.REPOS
import com.beginner.myapplication.data.db.UserDatabaseTable.UserColumns.Companion.TABLE_NAME
import com.beginner.myapplication.model.UserDetail
import java.sql.SQLException
class UserDatabase(ctx: Context) {

    val context = ctx

    private val userDatabase = UserDatabaseOpenHelper(context)
    private lateinit var database: SQLiteDatabase

    companion object {
        private var INSTANCE: UserDatabase? = null

        fun getInstance(context: Context): UserDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = UserDatabase(context)
                }
            }
            return INSTANCE!!
        }
    }

    @Throws(SQLException::class)
    fun openDB() {
        database = userDatabase.writableDatabase
    }

    fun closeDB() {
        database.close()

        if (database.isOpen) {
            database.close()
        }
    }

    fun queryAll(): Cursor {
        return database.query(
            TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            "$NAME ASC"
        )
    }

    fun insert(value: ContentValues): Long {
        return database.insert(TABLE_NAME, null, value)
    }

    fun insertUser(user: UserDetail): Long {
        val value = ContentValues()

        value.apply {
            put(AVATAR_URL, user.avatar_url)
            put(COMPANY, user.company)
            put(EMAIL, user.email)
            put(FOLLOWERS, user.followers)
            put(FOLLOWERS_URL, user.followers_url)
            put(FOLLOWING, user.following)
            put(FOLLOWING_URL, user.following_url)
            put(LOCATION, user.location)
            put(LOGIN, user.login)
            put(NAME, user.name)
            put(REPOS, user.public_repos)
        }

        return insert(value)
    }

    fun deleteByUsername(username: String): Int {
        return database.delete(
            TABLE_NAME,
            "$LOGIN = ?",
            arrayOf(username)
        )
    }

    fun deleteAllUser() {
        database.execSQL("DELETE FROM $TABLE_NAME")
    }

    fun update(username: String, values: ContentValues?): Int {
        return database.update(TABLE_NAME, values, "$LOGIN = ?", arrayOf(username))
    }

    fun getUsersList(): ArrayList<UserDetail> {
        val usersList = arrayListOf<UserDetail>()
        val usersCursor = queryAll()

        usersCursor.moveToFirst()

        if (usersCursor.count > 0) {
            do {
                val user = UserDetail(
                    name = usersCursor.getString(usersCursor.getColumnIndexOrThrow(NAME)),
                    email = usersCursor.getString(usersCursor.getColumnIndexOrThrow(EMAIL)),
                    login = usersCursor.getString(usersCursor.getColumnIndexOrThrow(LOGIN)),
                    public_repos = usersCursor.getInt(usersCursor.getColumnIndexOrThrow(REPOS)),
                    followers = usersCursor.getInt(usersCursor.getColumnIndexOrThrow(FOLLOWERS)),
                    followers_url = usersCursor.getString(usersCursor.getColumnIndexOrThrow(
                        FOLLOWERS_URL)),
                    following = usersCursor.getInt(usersCursor.getColumnIndexOrThrow(FOLLOWING)),
                    following_url = usersCursor.getString(usersCursor.getColumnIndexOrThrow(
                        FOLLOWING_URL)),
                    company = usersCursor.getString(usersCursor.getColumnIndexOrThrow(COMPANY)),
                    location = usersCursor.getString(usersCursor.getColumnIndexOrThrow(LOCATION)),
                    avatar_url = usersCursor.getString(usersCursor.getColumnIndexOrThrow(AVATAR_URL))
                )

                usersList.add(user)
            } while (usersCursor.moveToNext())
        }

        usersCursor.close()
        return usersList
    }
}

