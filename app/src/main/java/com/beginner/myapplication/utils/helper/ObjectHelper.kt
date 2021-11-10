package com.beginner.myapplication.utils.helper

import android.content.ContentValues
import android.database.Cursor
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
import com.beginner.myapplication.model.UserDetail

object ObjectHelper {
    fun getUsersList(usersCursor: Cursor?): ArrayList<UserDetail> {
        val usersList = arrayListOf<UserDetail>()
        if (usersCursor != null) {
            usersCursor.moveToFirst()

            if (usersCursor.count > 0) {
                do {
                    val user = UserDetail(
                        name = usersCursor.getString(usersCursor.getColumnIndexOrThrow(NAME)),
                        email = usersCursor.getString(usersCursor.getColumnIndexOrThrow(EMAIL)),
                        login = usersCursor.getString(usersCursor.getColumnIndexOrThrow(LOGIN)),
                        public_repos = usersCursor.getInt(usersCursor.getColumnIndexOrThrow(REPOS)),
                        followers = usersCursor.getInt(usersCursor.getColumnIndexOrThrow(FOLLOWERS)),
                        followers_url = usersCursor.getString(
                            usersCursor.getColumnIndexOrThrow(FOLLOWERS_URL)
                        ),
                        following = usersCursor.getInt(usersCursor.getColumnIndexOrThrow(FOLLOWING)),
                        following_url = usersCursor.getString(
                            usersCursor.getColumnIndexOrThrow(FOLLOWING_URL)
                        ),
                        company = usersCursor.getString(usersCursor.getColumnIndexOrThrow(COMPANY)),
                        location = usersCursor.getString(usersCursor.getColumnIndexOrThrow(LOCATION)),
                        avatar_url = usersCursor.getString(
                            usersCursor.getColumnIndexOrThrow(AVATAR_URL)
                        )
                    )

                    usersList.add(user)
                } while (usersCursor.moveToNext())
            }

            usersCursor.close()
        }
        return usersList
    }

    fun getContentValue(user: UserDetail): ContentValues {
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

        return value
    }
}