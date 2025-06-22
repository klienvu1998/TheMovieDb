package com.hyvu.themoviedb

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.hyvu.themoviedb.view.homescreen.MainActivity
import com.hyvu.themoviedb.view.loginscreen.LoginActivity

object AppNavigator {

    fun startLoginScreen(activity: Activity ,bundle: Bundle? = null) {
        activity.finish()
        val intent = Intent(activity, LoginActivity::class.java)
        intent.putExtras(bundle ?: Bundle())
        activity.startActivity(intent)
    }

    fun startHomeScreen(activity: Activity ,bundle: Bundle? = null) {
        activity.finish()
        val intent = Intent(activity, MainActivity::class.java)
        intent.putExtras(bundle ?: Bundle())
        activity.startActivity(intent)
    }

}