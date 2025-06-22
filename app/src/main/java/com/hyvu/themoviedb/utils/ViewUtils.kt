package com.hyvu.themoviedb.utils

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.view.ViewGroup
import android.widget.Toast


object ViewUtils {
    fun rotateView90(activity: Activity, view: ViewGroup, viewGroup: ViewGroup) {
    }
}

fun Context.showToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}