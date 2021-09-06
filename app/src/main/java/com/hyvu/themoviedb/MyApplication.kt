package com.hyvu.themoviedb

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.hyvu.themoviedb.di.AppComponent
import com.hyvu.themoviedb.di.DaggerAppComponent
import com.hyvu.themoviedb.storage.SharedPreferenceData
import com.hyvu.themoviedb.user.UserManager

open class MyApplication: Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(applicationContext)
    }

    val userManager by lazy {
        UserManager(SharedPreferenceData(applicationContext))
    }

    override fun onCreate() {
        super.onCreate()
    }
}