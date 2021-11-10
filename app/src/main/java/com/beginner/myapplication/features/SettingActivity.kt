package com.beginner.myapplication.features

import android.os.Bundle
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.beginner.myapplication.R
import com.beginner.myapplication.pref.AppPreferences
import com.beginner.myapplication.utils.handler.MainHandler
import com.beginner.myapplication.utils.helper.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SettingActivity : AppCompatActivity(), MainHandler {

    private lateinit var switchBtn: Switch
    private lateinit var notification: NotificationHelper
    private lateinit var appPreferences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        switchBtn = findViewById(R.id.switch_btn)
        notification = NotificationHelper(this)
        appPreferences = AppPreferences(this)
        switchBtn.isChecked = appPreferences.getNotifPref()

        turnOnNotification()

        switchBtn.setOnClickListener{
            if (switchBtn.isChecked) {
                appPreferences.setNotifPref(true)
                turnOnNotification()
            } else {
                appPreferences.setNotifPref(false)
                turnOffNotification()
            }
        }
    }

    private fun turnOnNotification() {
        if (switchBtn.isChecked) {
            if (!appPreferences.getNotifState()) {
                GlobalScope.launch(Dispatchers.Default) { notification.turnOnNotification() }
                appPreferences.setNotifState(true)
            }
        }
    }

    private fun turnOffNotification() {
        if (appPreferences.getNotifState()) {
            GlobalScope.launch(Dispatchers.Default) { notification.turnOffNotification() }
            appPreferences.setNotifState(false)
        }
    }

    override fun stopLoad() {
    }

    override fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun checkNetworkConn(): Boolean {
        return false
    }
}