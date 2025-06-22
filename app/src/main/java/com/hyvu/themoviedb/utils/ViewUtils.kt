package com.hyvu.themoviedb.utils

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast


object ViewUtils {
    fun rotateView90(activity: Activity, view: ViewGroup, viewGroup: ViewGroup) {
    }
}

fun Context.showToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Context.getDisplayMetrics(): DisplayMetrics {
    val displayMetrics = DisplayMetrics()
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val display = display // Access display from context
        display.getRealMetrics(displayMetrics)
    } else {
        // Below Android 11 (API 30)
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getMetrics(displayMetrics)
    }

    return displayMetrics
}