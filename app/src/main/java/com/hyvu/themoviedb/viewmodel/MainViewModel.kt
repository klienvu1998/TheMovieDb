package com.hyvu.themoviedb.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hyvu.themoviedb.data.entity.Cast
import com.hyvu.themoviedb.data.entity.Credits
import com.hyvu.themoviedb.data.entity.MovieFullDetails
import com.hyvu.themoviedb.data.entity.MovieVideos
import com.hyvu.themoviedb.data.repository.MovieRepository
import com.hyvu.themoviedb.di.scope.ActivityScope
import javax.inject.Inject

@ActivityScope
class MainViewModel @Inject constructor(val repository: MovieRepository): ViewModel() {

    val movieVideos: LiveData<MovieVideos> = repository.responseMovieVideos

    override fun onCleared() {
        super.onCleared()
        repository.deinit()
    }

}