package com.beginner.consumerapp.utils

import android.os.Parcelable
import com.beginner.consumerapp.model.UserDetail
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserResource(var favState: Boolean, val data: UserDetail): Parcelable {
    companion object {
        fun addUserToFav(userData: UserDetail): UserResource {
            return UserResource(true, userData)
        }
    }
}