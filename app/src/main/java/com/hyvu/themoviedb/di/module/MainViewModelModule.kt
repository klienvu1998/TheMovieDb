package com.hyvu.themoviedb.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hyvu.themoviedb.di.scope.ActivityScope
import com.hyvu.themoviedb.di.scope.ViewModelKey
import com.hyvu.themoviedb.viewmodel.*
import com.hyvu.themoviedb.viewmodel.factory.MainViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import javax.inject.Singleton

@Module
abstract class MainViewModelModule {

    @Binds
    abstract fun viewModelFactory(factory: MainViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun provideMainViewModel(viewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun provideHomeViewModel(viewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MovieInfoViewModel::class)
    abstract fun provideMovieInfoViewModel(viewModel: MovieInfoViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TikMovieViewModel::class)
    abstract fun provideTikMovieViewModel(viewModel: TikMovieViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CommentViewModel::class)
    abstract fun provideCommentViewModel(viewModel: CommentViewModel): ViewModel
}