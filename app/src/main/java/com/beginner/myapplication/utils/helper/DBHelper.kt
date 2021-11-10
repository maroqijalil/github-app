package com.beginner.myapplication.utils.helper

import com.beginner.myapplication.model.UserDetail

interface DBHelper {
    fun insertUser(user: UserDetail)
    fun showAlert(title: String, msg: String, user: UserDetail, pos: Int)
}