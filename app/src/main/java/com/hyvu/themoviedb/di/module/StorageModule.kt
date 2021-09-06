package com.hyvu.themoviedb.di.module

import com.hyvu.themoviedb.storage.SharedPreferenceData
import com.hyvu.themoviedb.storage.Storage
import dagger.Binds
import dagger.Module

@Module
abstract class StorageModule {
    @Binds
    abstract fun provideStorage(storage: SharedPreferenceData): Storage
}