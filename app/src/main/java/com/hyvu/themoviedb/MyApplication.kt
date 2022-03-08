package com.hyvu.themoviedb

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.hyvu.themoviedb.di.AppComponent
import com.hyvu.themoviedb.di.DaggerAppComponent
import com.hyvu.themoviedb.utils.SharedPreferenceData
import com.hyvu.themoviedb.utils.UserManager

open class MyApplication: Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(applicationContext)
    }

    val userManager by lazy {
        UserManager(SharedPreferenceData(applicationContext))
    }

    override fun onCreate() {
        if (userManager.isNightMode) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate()
    }
}