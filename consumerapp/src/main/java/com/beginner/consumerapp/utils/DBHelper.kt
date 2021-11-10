package com.beginner.consumerapp.utils

import com.beginner.consumerapp.model.UserDetail

interface DBHelper {
    fun updateFavList()
    fun showAlert(title: String, msg: String, user: UserDetail, pos: Int)
}