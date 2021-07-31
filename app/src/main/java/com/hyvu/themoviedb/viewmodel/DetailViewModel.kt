package com.hyvu.themoviedb.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hyvu.themoviedb.data.entity.Credits
import com.hyvu.themoviedb.data.entity.MovieFullDetails
import com.hyvu.themoviedb.data.entity.MovieVideos
import com.hyvu.themoviedb.data.repository.MovieRepository

class DetailViewModel: ViewModel() {

    val movieFullDetails: LiveData<MovieFullDetails> = MovieRepository.responseCurrentMovieDetail
    val movieVideos: LiveData<MovieVideos> = MovieRepository.responseMovieVideos
    val movieCredits: LiveData<Credits> = MovieRepository.responseMovieCredits

    fun fetchMovieCredits(movieId: Int) {
        MovieRepository.fetchMovieCredits(movieId)
    }

    override fun onCleared() {
        super.onCleared()
    }
}