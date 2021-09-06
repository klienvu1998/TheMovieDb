package com.hyvu.themoviedb.di.scope

import com.hyvu.themoviedb.view.activity.LoginActivity
import com.hyvu.themoviedb.view.activity.MainActivity
import com.hyvu.themoviedb.view.activity.MovieImageActivity
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