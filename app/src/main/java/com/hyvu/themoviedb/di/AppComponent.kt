package com.hyvu.themoviedb.di

import android.content.Context
import com.hyvu.themoviedb.di.module.AppModule
import com.hyvu.themoviedb.di.module.DataSourceModule
import com.hyvu.themoviedb.di.module.HomeMovieDatabaseModule
import com.hyvu.themoviedb.di.module.RetrofitModule
import com.hyvu.themoviedb.di.module.StorageModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [StorageModule::class, AppSubComponent::class, RetrofitModule::class, HomeMovieDatabaseModule::class, DataSourceModule::class, AppModule::class])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    fun mainComponent(): MainComponent.Factory
    fun imageComponent(): MovieImageComponent.Factory
    fun loginComponent(): LoginComponent.Factory
    fun splashComponent(): SplashScreenComponent.Factory
}