package com.hyvu.themoviedb.di.module

import com.hyvu.themoviedb.utils.SharedPreferenceData
import com.hyvu.themoviedb.utils.Storage
import dagger.Binds
import dagger.Module

@Module
abstract class StorageModule {
    @Binds
    abstract fun provideStorage(storage: SharedPreferenceData): Storage
}