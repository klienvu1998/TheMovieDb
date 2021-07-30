package com.hyvu.themoviedb.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hyvu.themoviedb.data.entity.*
import com.hyvu.themoviedb.data.repository.MovieRepository

class HomeViewModel: ViewModel() {

    val genres: LiveData<Genres> = MovieRepository.responseListMovieGenre
    val listOverviewMovies: LiveData<Map<Genre, MoviesByGenre>> = MovieRepository.responseMovieByGenre

    fun fetchOverviewMovie() {
        MovieRepository.fetchListMovieGenres()
    }

}