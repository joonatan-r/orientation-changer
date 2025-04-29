package com.example.overlay

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.PixelFormat
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
import com.example.overlay.ui.theme.OverlayTheme


class MainActivity : ComponentActivity() {

    private var orientationChanger: LinearLayout? = null
    private var orientationLayout: WindowManager.LayoutParams? = null

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

        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        wm.addView(orientationChanger, orientationLayout)

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
        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        if (orientationLayout?.screenOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            orientationLayout?.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            wm.updateViewLayout(orientationChanger, orientationLayout)
        } else {
            orientationLayout?.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            wm.updateViewLayout(orientationChanger, orientationLayout)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("SAVED_LAYOUT", orientationLayout?.screenOrientation ?: ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
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
