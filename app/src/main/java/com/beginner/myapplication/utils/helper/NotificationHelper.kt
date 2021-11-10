package com.beginner.myapplication.utils.helper

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.beginner.myapplication.R
import com.beginner.myapplication.receiver.NotificationReceiver
import com.beginner.myapplication.utils.handler.MainHandler
import java.text.SimpleDateFormat
import java.util.*

class NotificationHelper(ctx: Context) {

    val context = ctx

    private val alarmManager: AlarmManager
    private val intent: Intent
    private val calendar: Calendar

    private val title = context.getString(R.string.notif_title)
    private val description = context.getString(R.string.notif_desc)

    companion object {
        const val NOTIF_TITLE = "notification_title"
        const val NOTIF_DES = "notification_desc"
        const val NOTIF_ID = 69
    }

    init {
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        intent = Intent(context, NotificationReceiver::class.java)
        calendar = Calendar.getInstance()
    }

    @SuppressLint("SimpleDateFormat")
    fun turnOnNotification() {
        intent.putExtra(NOTIF_TITLE, title)
        intent.putExtra(NOTIF_DES, description)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            NOTIF_ID, intent, 0
        )

        calendar.set(Calendar.HOUR_OF_DAY, 9)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )

        (context as MainHandler).showToast(
            context.getString(R.string.on_notif) + " " + SimpleDateFormat("HH:mm").format(
                calendar.time
            )
        )
    }

    fun turnOffNotification() {
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            NOTIF_ID, intent, 0
        )
        pendingIntent.cancel()
        alarmManager.cancel(pendingIntent)

        (context as MainHandler).showToast(context.getString(R.string.off_notif))
    }
}