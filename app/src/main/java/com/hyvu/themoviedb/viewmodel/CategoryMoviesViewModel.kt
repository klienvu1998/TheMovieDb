package com.hyvu.themoviedb.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.rxjava2.cachedIn
import com.hyvu.themoviedb.data.entity.Genre
import com.hyvu.themoviedb.data.entity.MovieDetail
import com.hyvu.themoviedb.data.entity.MoviesListResponse
import com.hyvu.themoviedb.data.repository.MovieRepository
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CategoryMoviesViewModel @Inject constructor(val repository: MovieRepository): ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val _responseMovies: MutableLiveData<PagingData<MovieDetail>> = MutableLiveData()
    val responseMovies: LiveData<PagingData<MovieDetail>> = _responseMovies

    val favoriteList: LiveData<MoviesListResponse> = repository.favoriteList
    val watchList: LiveData<MoviesListResponse> = repository.watchList

    @ExperimentalPagingApi
    fun getMovieByGenre(genreId: Int) {
        compositeDisposable.add(
                repository.fetchMovieByGenre(genreId).cachedIn(viewModelScope)
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            _responseMovies.postValue(it)
                        }, { e ->
                            e.printStackTrace()
                        })
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}