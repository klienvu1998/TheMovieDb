package com.hyvu.themoviedb.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hyvu.themoviedb.di.scope.ActivityScope
import com.hyvu.themoviedb.viewmodel.MainViewModel
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@ActivityScope
class MainViewModelFactory @Inject constructor(
        private val viewModels: MutableMap<Class<out ViewModel>, Provider<ViewModel>>
): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T = viewModels[modelClass]?.get() as T
}