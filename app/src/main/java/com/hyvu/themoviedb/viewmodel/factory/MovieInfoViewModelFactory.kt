package com.hyvu.themoviedb.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hyvu.themoviedb.viewmodel.MovieInfoViewModel

class MovieInfoViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MovieInfoViewModel() as T
    }
}