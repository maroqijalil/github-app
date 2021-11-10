package com.beginner.myapplication.model

import android.os.Parcelable
import com.beginner.myapplication.R
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val avatar: String = "",
    val company: String = "",
    val follower: Int = 0,
    val following: Int = 0,
    val location: String = "",
    val name: String = "",
    val repository: Int = 0,
    val username: String = ""
) : Parcelable

object UserDrawable {
    val drawable: Map<String, Int> = mapOf(
        "user1" to R.drawable.user1,
        "user2" to R.drawable.user2,
        "user3" to R.drawable.user3,
        "user4" to R.drawable.user4,
        "user5" to R.drawable.user5,
        "user6" to R.drawable.user6,
        "user7" to R.drawable.user7,
        "user8" to R.drawable.user8,
        "user9" to R.drawable.user9,
        "user10" to R.drawable.user10
    )
}