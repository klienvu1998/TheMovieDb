package com.hyvu.themoviedb.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hyvu.themoviedb.data.entity.MovieDetails
import com.hyvu.themoviedb.data.entity.MovieVideo
import com.hyvu.themoviedb.data.repository.MovieRepository

class MainViewModel(): ViewModel() {

    val movieVideos: LiveData<MovieVideo> = MovieRepository.responseMovieVideos

    val movieDetails: LiveData<MovieDetails> = MovieRepository.responseCurrentMovieDetail

    fun fetchMovieDetails(movieId: Int) {
        MovieRepository.fetchMovieDetails(movieId)
    }

    override fun onCleared() {
        super.onCleared()
    }

}