package com.beginner.myapplication.data.db

import android.net.Uri
import android.provider.BaseColumns

internal class UserDatabaseTable {
    companion object {
        const val AUTHORITY = "com.beginner.myapplication"
        const val SCHEME = "content"
    }
    internal class UserColumns: BaseColumns {
        companion object {
            const val TABLE_NAME = "user_table"
            const val AVATAR_URL = "avatar_url"
            const val COMPANY = "company"
            const val EMAIL = "email"
            const val FOLLOWERS = "followers"
            const val FOLLOWERS_URL = "followers_url"
            const val FOLLOWING = "following"
            const val FOLLOWING_URL = "following_url"
            const val _ID = "_id"
            const val LOCATION = "location"
            const val LOGIN = "login"
            const val NAME = "name"
            const val REPOS = "public_repos"

            val USER_CONTENT_URI: Uri = Uri.Builder().scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(TABLE_NAME)
                .build()
        }
    }
}