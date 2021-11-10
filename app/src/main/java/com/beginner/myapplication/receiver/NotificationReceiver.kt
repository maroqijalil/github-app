package com.beginner.myapplication.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import com.beginner.myapplication.R
import com.beginner.myapplication.features.main.MainActivity
import com.beginner.myapplication.utils.helper.NotificationHelper

class NotificationReceiver: BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = "Channel_1"
        const val CHANNEL_NAME = "Notification User Channel"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val title = intent?.getStringExtra(NotificationHelper.NOTIF_TITLE)
        val description = intent?.getStringExtra(NotificationHelper.NOTIF_DES)

        showNotification(context, title, description)
    }

    private fun showNotification(context: Context?, title: String?, desc: String?) {
        val notifIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, notifIntent, 0)
        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notifBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications_white_48px)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources,
                R.drawable.github_mark_120px_plus
            ))
            .setContentTitle(title)
            .setContentText(desc)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT)

            notifBuilder.setChannelId(CHANNEL_ID)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(NotificationHelper.NOTIF_ID, notifBuilder.build())
    }
}