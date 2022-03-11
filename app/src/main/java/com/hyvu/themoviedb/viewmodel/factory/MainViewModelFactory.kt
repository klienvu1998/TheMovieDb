package com.hyvu.themoviedb.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hyvu.themoviedb.di.scope.ActivityScope
import javax.inject.Inject
import javax.inject.Provider

@ActivityScope
class MainViewModelFactory @Inject constructor(
        private val viewModels: MutableMap<Class<out ViewModel>, Provider<ViewModel>>
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return viewModels[modelClass]?.get() as T
    }
}