package com.hyvu.themoviedb.di

import com.hyvu.themoviedb.di.scope.ActivityScope
import com.hyvu.themoviedb.view.splashscreen.SplashActivity
import dagger.Subcomponent

@ActivityScope
@Subcomponent
interface SplashScreenComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): SplashScreenComponent
    }

    fun inject(activity: SplashActivity)

}