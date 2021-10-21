package com.hyvu.themoviedb.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hyvu.themoviedb.data.entity.*
import com.hyvu.themoviedb.data.repository.MovieRepository
import com.hyvu.themoviedb.di.scope.ActivityScope
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@ActivityScope
class HomeViewModel @Inject constructor(private val repository: MovieRepository): ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    val genres: LiveData<List<Genre>> = repository.responseListMovieGenre
    val listOverviewMovies: LiveData<Map<Genre, List<MovieDetail>>> = repository.responseMovieByGenre
    val listTrendingMovies: LiveData<TrendingMovies> = repository.responseTrendingMovies

    fun fetchHomeListMovieByGenres() {
        repository.fetchHomeListMovieByGenres()
    }

    fun fetchTrendingMovies() {
        repository.fetchTrendingMovie()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    fun fetchDatabase() {
        repository.getGenresInDatabase()
    }

    fun getListMovieDetailByGenre(genres: List<Genre>) {
        repository.getListMovieDetailByGenreInDatabase(genres)
    }

}