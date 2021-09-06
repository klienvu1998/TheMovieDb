package com.hyvu.themoviedb.di

import com.hyvu.themoviedb.di.module.LoginViewModelModule
import com.hyvu.themoviedb.di.scope.ActivityScope
import com.hyvu.themoviedb.view.activity.LoginActivity
import dagger.Subcomponent

@ActivityScope
@Subcomponent(modules = [LoginViewModelModule::class])
interface LoginComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): LoginComponent
    }

    fun inject(activity: LoginActivity)

}