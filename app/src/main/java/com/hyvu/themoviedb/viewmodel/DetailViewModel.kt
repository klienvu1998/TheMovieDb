package com.hyvu.themoviedb.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hyvu.themoviedb.data.entity.MovieDetails
import com.hyvu.themoviedb.data.entity.MovieVideo
import com.hyvu.themoviedb.data.repository.MovieRepository

class DetailViewModel: ViewModel() {

    val movieDetails: LiveData<MovieDetails> = MovieRepository.responseCurrentMovieDetail
    val movieVideos: LiveData<MovieVideo> = MovieRepository.responseMovieVideos

    override fun onCleared() {
        super.onCleared()
    }
}