package com.hyvu.themoviedb.di.scope

import com.hyvu.themoviedb.view.loginscreen.LoginActivity
import com.hyvu.themoviedb.view.homescreen.MainActivity
import com.hyvu.themoviedb.view.imagescreen.MovieImageActivity
import dagger.Subcomponent

@ActivityScope
@Subcomponent
interface UserComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): UserComponent
    }

    fun inject(activity: MainActivity)
    fun inject(activity: LoginActivity)
    fun inject(activity: MovieImageActivity)
}