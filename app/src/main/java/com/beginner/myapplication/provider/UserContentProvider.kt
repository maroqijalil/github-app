package com.beginner.myapplication.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.content.UriMatcher.NO_MATCH
import android.database.Cursor
import android.net.Uri
import com.beginner.myapplication.data.db.UserDatabase
import com.beginner.myapplication.data.db.UserDatabaseTable.Companion.AUTHORITY
import com.beginner.myapplication.data.db.UserDatabaseTable.UserColumns.Companion.TABLE_NAME
import com.beginner.myapplication.data.db.UserDatabaseTable.UserColumns.Companion.USER_CONTENT_URI

class UserContentProvider : ContentProvider() {

    private lateinit var userDatabase: UserDatabase
    private val uriMatcher = UriMatcher(NO_MATCH)

    companion object {
        private const val ALL = 1
        private const val BY_NAME = 2
    }

    init {
        uriMatcher.addURI(AUTHORITY, TABLE_NAME,
            ALL
        )
        uriMatcher.addURI(AUTHORITY, "$TABLE_NAME/*",
            BY_NAME
        )
    }

    override fun onCreate(): Boolean {
        userDatabase = UserDatabase.getInstance(context as Context)
        userDatabase.openDB()
        return true
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val itemDel = when(uriMatcher.match(uri)) {
            BY_NAME -> userDatabase.deleteByUsername(uri.lastPathSegment.toString())
            else -> 0
        }

        context?.contentResolver?.notifyChange(USER_CONTENT_URI, null)
        return itemDel
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val itemAdd = when (uriMatcher.match(uri)) {
            ALL -> userDatabase.insert(values!!)
            else -> 0
        }

        context?.contentResolver?.notifyChange(USER_CONTENT_URI, null)
        return Uri.parse("$USER_CONTENT_URI/$itemAdd")
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        return when (uriMatcher.match(uri)) {
            ALL -> userDatabase.queryAll()
            else -> null
        }
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        return when (uriMatcher.match(uri)) {
            BY_NAME -> userDatabase.update(uri.lastPathSegment.toString(), values)
            else -> 0
        }
    }
}
