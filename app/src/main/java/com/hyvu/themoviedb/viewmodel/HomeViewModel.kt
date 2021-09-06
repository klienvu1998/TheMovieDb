package com.hyvu.themoviedb.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyvu.themoviedb.data.entity.*
import com.hyvu.themoviedb.data.repository.HomeMovieDatabaseRepository
import com.hyvu.themoviedb.data.repository.MovieRepository
import com.hyvu.themoviedb.di.scope.ActivityScope
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.launch
import javax.inject.Inject

@ActivityScope
class HomeViewModel @Inject constructor(val repository: MovieRepository, val homeMovieDatabaseRepository: HomeMovieDatabaseRepository): ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    val genres: LiveData<Genres> = repository.responseListMovieGenre
    val listOverviewMovies: LiveData<Pair<Genre, List<MovieDetail>>> = repository.responseMovieByGenre
    val listTrendingMovies: LiveData<TrendingMovies> = repository.responseTrendingMovies

    fun fetchHomeListMovieByGenres() {
        repository.fetchHomeListMovieByGenres()
    }

    fun fetchTrendingMovies() {
        repository.fetchTrendingMovie()
    }

    fun insertMovieDetailToDatabase(movieDetail: MovieDetail) {
        homeMovieDatabaseRepository.insert(movieDetail)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
        homeMovieDatabaseRepository.clear()
    }

}