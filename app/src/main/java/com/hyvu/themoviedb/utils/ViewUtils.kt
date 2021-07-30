package com.hyvu.themoviedb.utils

import android.app.Activity
import android.content.Context
import android.graphics.Insets
import android.os.Build
import android.util.DisplayMetrics
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowMetrics
import androidx.constraintlayout.widget.ConstraintLayout


object ViewUtils {
    fun rotateView90(activity: Activity, view: ViewGroup, viewGroup: ViewGroup) {
        var height = viewGroup.height
        var width = viewGroup.width
        val lp = view.layoutParams
        lp.width = height
        lp.height = width

    }
}