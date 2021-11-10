package com.beginner.myapplication.utils

import android.os.Parcelable
import com.beginner.myapplication.model.UserDetail
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserResource(var favState: Boolean, val data: UserDetail): Parcelable {
    companion object {
        fun addUserToFav(userData: UserDetail): UserResource {
            return UserResource(true, userData)
        }

        fun addUser(userData: UserDetail): UserResource {
            return UserResource(false, userData)
        }
    }
}