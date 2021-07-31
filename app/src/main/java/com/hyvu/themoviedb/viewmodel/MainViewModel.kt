package com.hyvu.themoviedb.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hyvu.themoviedb.data.entity.Cast
import com.hyvu.themoviedb.data.entity.Credits
import com.hyvu.themoviedb.data.entity.MovieFullDetails
import com.hyvu.themoviedb.data.entity.MovieVideos
import com.hyvu.themoviedb.data.repository.MovieRepository

class MainViewModel(): ViewModel() {

    val movieVideos: LiveData<MovieVideos> = MovieRepository.responseMovieVideos
    val movieFullDetails: LiveData<MovieFullDetails> = MovieRepository.responseCurrentMovieDetail

    fun fetchMovieDetails(movieId: Int) {
        MovieRepository.fetchMovieDetails(movieId)
    }

    override fun onCleared() {
        super.onCleared()
    }

}