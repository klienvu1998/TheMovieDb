package com.hyvu.themoviedb.di

import com.hyvu.themoviedb.di.scope.ActivityScope
import com.hyvu.themoviedb.view.image.MovieImageActivity
import dagger.Subcomponent

@ActivityScope
@Subcomponent()
interface MovieImageComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): MovieImageComponent
    }

    fun inject(movieImageActivity: MovieImageActivity)

}