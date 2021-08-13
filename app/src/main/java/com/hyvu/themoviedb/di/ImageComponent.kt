package com.hyvu.themoviedb.di

import com.hyvu.themoviedb.di.scope.ActivityScope
import com.hyvu.themoviedb.view.activity.MovieImageActivity
import dagger.Subcomponent

@ActivityScope
@Subcomponent()
interface ImageComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): ImageComponent
    }

    fun inject(movieImageActivity: MovieImageActivity)

}