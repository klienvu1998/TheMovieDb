package com.hyvu.themoviedb.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.rxjava2.cachedIn
import com.hyvu.themoviedb.data.entity.*
import com.hyvu.themoviedb.data.repository.MovieRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class HomeViewModel: ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    val genres: LiveData<Genres> = MovieRepository.responseListMovieGenre
    val listOverviewMovies: LiveData<Map<Genre, List<MovieDetail>>> = MovieRepository.responseMovieByGenre
    val listTrendingMovies: LiveData<TrendingMovies> = MovieRepository.responseTrendingMovies
    private val _responseMovies: MutableLiveData<PagingData<MovieDetail>> = MutableLiveData()
    val responseMovies: LiveData<PagingData<MovieDetail>> = _responseMovies

    fun getMoviesPerPage(genreId: Int) {
        compositeDisposable.add(
            MovieRepository.fetchMovieByGenrePerPage(genreId).cachedIn(viewModelScope)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _responseMovies.postValue(it)
                }, { e ->
                    e.printStackTrace()
                })
        )
    }

    fun fetchHomeListMovieByGenres() {
        MovieRepository.fetchHomeListMovieByGenres()
    }

    fun fetchTrendingMovies() {
        MovieRepository.fetchTrendingMovie()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}