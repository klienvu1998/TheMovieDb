package com.hyvu.themoviedb.di

import com.hyvu.themoviedb.di.module.MainViewModelModule
import com.hyvu.themoviedb.di.scope.ActivityScope
import com.hyvu.themoviedb.view.homescreen.UserSettingsFragment
import com.hyvu.themoviedb.view.homescreen.*
import dagger.Subcomponent

@ActivityScope
@Subcomponent(modules = [MainViewModelModule::class])
interface MainComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): MainComponent
    }

    fun inject(activity: MainActivity)
    fun inject(fragment: HomeContainerFragment)
    fun inject(fragment: CastFragment)
    fun inject(fragment: CommentFragment)
    fun inject(fragment: DetailFragment)
    fun inject(fragment: MovieInfoFragment)
    fun inject(fragment: MoviesByGenreFragment)
    fun inject(fragment: HomeFragment)
    fun inject(fragment: TikMovieFragment)
    fun inject(fragment: UserFragment)
    fun inject(fragment: UserSettingsFragment)
    fun inject(fragment: UserHomeFragment)
}