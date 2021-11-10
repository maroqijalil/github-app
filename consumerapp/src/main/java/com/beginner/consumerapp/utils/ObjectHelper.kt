package com.beginner.consumerapp.utils

import android.database.Cursor
import com.beginner.consumerapp.data.db.UserDatabaseTable.UserColumns.Companion.AVATAR_URL
import com.beginner.consumerapp.data.db.UserDatabaseTable.UserColumns.Companion.COMPANY
import com.beginner.consumerapp.data.db.UserDatabaseTable.UserColumns.Companion.EMAIL
import com.beginner.consumerapp.data.db.UserDatabaseTable.UserColumns.Companion.FOLLOWERS
import com.beginner.consumerapp.data.db.UserDatabaseTable.UserColumns.Companion.FOLLOWERS_URL
import com.beginner.consumerapp.data.db.UserDatabaseTable.UserColumns.Companion.FOLLOWING
import com.beginner.consumerapp.data.db.UserDatabaseTable.UserColumns.Companion.FOLLOWING_URL
import com.beginner.consumerapp.data.db.UserDatabaseTable.UserColumns.Companion.LOCATION
import com.beginner.consumerapp.data.db.UserDatabaseTable.UserColumns.Companion.LOGIN
import com.beginner.consumerapp.data.db.UserDatabaseTable.UserColumns.Companion.NAME
import com.beginner.consumerapp.data.db.UserDatabaseTable.UserColumns.Companion.REPOS
import com.beginner.consumerapp.model.UserDetail

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
                            usersCursor.getColumnIndexOrThrow(
                                FOLLOWERS_URL
                            )
                        ),
                        following = usersCursor.getInt(usersCursor.getColumnIndexOrThrow(FOLLOWING)),
                        following_url = usersCursor.getString(
                            usersCursor.getColumnIndexOrThrow(
                                FOLLOWING_URL
                            )
                        ),
                        company = usersCursor.getString(usersCursor.getColumnIndexOrThrow(COMPANY)),
                        location = usersCursor.getString(usersCursor.getColumnIndexOrThrow(LOCATION)),
                        avatar_url = usersCursor.getString(
                            usersCursor.getColumnIndexOrThrow(
                                AVATAR_URL
                            )
                        )
                    )

                    usersList.add(user)
                } while (usersCursor.moveToNext())
            }

            usersCursor.close()
        }
        return usersList
    }
}