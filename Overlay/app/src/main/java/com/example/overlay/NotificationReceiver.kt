package com.example.overlay

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity.NOTIFICATION_SERVICE

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.getStringExtra("action")
        if (action == "toggle") {
            context?.sendBroadcast(Intent("TOGGLE"))
        } else if (action == "remove") {
            context?.sendBroadcast(Intent("REMOVE"))
            val mNotificationManager = context?.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            mNotificationManager.cancelAll()
        }
    }

}