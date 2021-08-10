package com.hyvu.themoviedb.di

import android.content.Context
import com.hyvu.themoviedb.di.module.RetrofitModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppSubComponent::class, RetrofitModule::class])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }
    fun mainComponent(): MainComponent.Factory
}