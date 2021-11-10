package com.beginner.myapplication.utils.handler

import com.beginner.myapplication.model.UserDetail

interface NavigationHandler {
    fun navigateTo(user: UserDetail)
}