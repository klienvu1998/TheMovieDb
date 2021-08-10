package com.hyvu.themoviedb

import android.app.Application
import com.hyvu.themoviedb.di.AppComponent
import com.hyvu.themoviedb.di.DaggerAppComponent

open class MyApplication: Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(applicationContext)
    }

}