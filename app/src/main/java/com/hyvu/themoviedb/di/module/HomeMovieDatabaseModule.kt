package com.hyvu.themoviedb.di.module

import android.app.Application
import android.content.Context
import com.hyvu.themoviedb.data.repository.HomeMovieDatabaseRepository
import com.hyvu.themoviedb.database.HomeDatabase
import com.hyvu.themoviedb.database.HomeMovieDetailDao
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class HomeMovieDatabaseModule {

    @Singleton
    @Provides
    fun provideDb(context: Context): HomeDatabase {
        return HomeDatabase.getDatabase(context)
    }

    @Singleton
    @Provides
    fun provideLoginDao(db: HomeDatabase): HomeMovieDetailDao {
        return db.homeMovieDetailDao()
    }
}