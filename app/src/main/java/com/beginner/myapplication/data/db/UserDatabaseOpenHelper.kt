package com.beginner.myapplication.data.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
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
import com.beginner.myapplication.data.db.UserDatabaseTable.UserColumns.Companion._ID

class UserDatabaseOpenHelper(ctx: Context): SQLiteOpenHelper(ctx, DB_NAME, null, DB_VER) {
    companion object {
        private const val DB_NAME = "user_database"
        private const val DB_VER = 1
        private const val USER_DB_TABLE = "CREATE TABLE $TABLE_NAME" +
                "($_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$AVATAR_URL TEXT NOT NULL," +
                "$COMPANY TEXT," +
                "$EMAIL TEXT," +
                "$FOLLOWERS INTEGER NOT NULL," +
                "$FOLLOWERS_URL TEXT," +
                "$FOLLOWING INTEGER NOT NULL," +
                "$FOLLOWING_URL TEXT," +
                "$LOCATION TEXT," +
                "$LOGIN TEXT NOT NULL," +
                "$NAME TEXT," +
                "$REPOS INTEGER NOT NULL)"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(USER_DB_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
}