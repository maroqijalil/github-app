package com.beginner.myapplication.pref

import android.content.Context
import android.content.Context.MODE_PRIVATE

class AppPreferences(ctx: Context) {
    
    val context = ctx
    
    private val shrdPreferences = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
    private var notifPref = shrdPreferences.getBoolean(NOTIF_PREFS, true)
    private var notifStatePref = shrdPreferences.getBoolean(NOTIF_STATE, false)
    
    companion object {
        const val PREFS_NAME = "user_preferences"
        const val NOTIF_PREFS = "notif_preference"
        const val NOTIF_STATE = "notif_state"
    }
    
    fun setNotifPref(set: Boolean) {
        val editor = shrdPreferences.edit()
        editor.putBoolean(NOTIF_PREFS, set)
        editor.apply()

        notifPref = set
    }
    
    fun getNotifPref(): Boolean {
        notifPref = shrdPreferences.getBoolean(NOTIF_PREFS, true)
        return notifPref
    }
    
    fun setNotifState(set: Boolean) {
        val editor = shrdPreferences.edit()
        editor.putBoolean(NOTIF_STATE, set)
        editor.apply()

        notifStatePref = set
    }

    fun getNotifState(): Boolean {
        notifStatePref = shrdPreferences.getBoolean(NOTIF_STATE, false)
        return notifStatePref
    }
}