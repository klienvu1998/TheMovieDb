package com.hyvu.themoviedb.di.module

import com.hyvu.themoviedb.data.local.datasource.AuthenticateLocalDataSource
import com.hyvu.themoviedb.data.local.datasource.AuthenticateLocalDataSourceImpl
import com.hyvu.themoviedb.data.remote.datasource.AuthenticateRemoteDataSource
import com.hyvu.themoviedb.data.remote.datasource.AuthenticateRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class DataSourceModule {

    @Binds
    @Singleton
    abstract fun bindAuthenticateRemoteDataSource(impl: AuthenticateRemoteDataSourceImpl): AuthenticateRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindAuthenticateLocalDataSource(impl: AuthenticateLocalDataSourceImpl): AuthenticateLocalDataSource

}