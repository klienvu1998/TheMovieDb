package com.hyvu.themoviedb.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.rxjava2.cachedIn
import com.hyvu.themoviedb.data.entity.MovieDetail
import com.hyvu.themoviedb.data.entity.MoviesListResponse
import com.hyvu.themoviedb.data.repository.MovieRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class CategoryMoviesViewModel @Inject constructor(val repository: MovieRepository): ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val _responseMovies: MutableLiveData<PagingData<MovieDetail>> = MutableLiveData()
    val responseMovies: LiveData<PagingData<MovieDetail>> = _responseMovies

    val favoriteList: LiveData<MoviesListResponse> = repository.favoriteList

    fun getMoviesPerPage(genreId: Int) {
        compositeDisposable.add(
                repository.fetchMovieByGenrePerPage(genreId).cachedIn(viewModelScope)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
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