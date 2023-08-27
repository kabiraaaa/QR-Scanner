package com.example.myapplication.utils

import android.view.View
import android.view.Window
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat


object CommonFunctionality {
    fun fullScreen(window: Window, decorView: View) {
        //Code for full screen mode, hiding navigation bar and changing behaviour with swipe
        // ref- https://developer.android.com/develop/ui/views/layout/immersive#java
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())
    }
}