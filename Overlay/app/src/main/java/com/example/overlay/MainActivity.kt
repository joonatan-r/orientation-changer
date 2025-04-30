package com.example.overlay

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.graphics.PixelFormat
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.app.NotificationCompat
import com.example.overlay.ui.theme.OverlayTheme


class MainActivity : ComponentActivity() {

    private var orientationChanger: LinearLayout? = null
    private var orientationLayout: WindowManager.LayoutParams? = null
    private var toggleBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            change()
        }
    }
    private var removeBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            finish()
        }
    }

    @SuppressLint("InlinedApi", "UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        orientationChanger = LinearLayout(this)
        orientationLayout = WindowManager.LayoutParams(
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.RGBA_8888
        )

        if (savedInstanceState == null) {
            orientationLayout?.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            orientationLayout?.screenOrientation = savedInstanceState.getInt("SAVED_LAYOUT")
        }
        orientationLayout?.alpha = 0f
        orientationChanger?.visibility = View.VISIBLE
        orientationChanger?.alpha = 0f

        val wm = applicationContext.getSystemService(WINDOW_SERVICE) as WindowManager
        wm.addView(orientationChanger, orientationLayout)
        makeNotification()
        registerReceiver(removeBroadcastReceiver, IntentFilter("REMOVE"), RECEIVER_EXPORTED)
        registerReceiver(toggleBroadcastReceiver, IntentFilter("TOGGLE"), RECEIVER_EXPORTED)

        setContent {
            OverlayTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    Greeting(
                        onClickHandler = { change() }
                    )
                }
            }
        }
    }

    private fun change() {
        val wm = applicationContext.getSystemService(WINDOW_SERVICE) as WindowManager
        if (orientationLayout?.screenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            orientationLayout?.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            orientationLayout?.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
        wm.updateViewLayout(orientationChanger, orientationLayout)
    }

    private fun makeNotification() {
        val intentAction1 = Intent(this, NotificationReceiver::class.java)
        intentAction1.putExtra("action", "toggle")
        val pendingIntent1 = PendingIntent.getBroadcast(this, 1, intentAction1, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val action1 = NotificationCompat.Action(R.drawable.ic_launcher_foreground, "Toggle", pendingIntent1)

        val intentAction2 = Intent(this, NotificationReceiver::class.java)
        intentAction2.putExtra("action", "remove")
        val pendingIntent2 = PendingIntent.getBroadcast(this, 2, intentAction2, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val action2 = NotificationCompat.Action(R.drawable.ic_launcher_foreground, "Remove", pendingIntent2)
        val mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "MY_CHANNEL",
                "Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            mNotificationManager.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(this, "MY_CHANNEL")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .addAction(action1)
            .addAction(action2)
            .build()
        mNotificationManager.notify(0, notification)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("SAVED_LAYOUT", orientationLayout?.screenOrientation ?: ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(toggleBroadcastReceiver)
        unregisterReceiver(removeBroadcastReceiver)
    }
}

@Composable
fun Greeting(onClickHandler: () -> Unit) {
    Button(
        onClick = {
            onClickHandler()
        }
    ) {
        Text(
            text = "Toggle"
        )
    }
}
