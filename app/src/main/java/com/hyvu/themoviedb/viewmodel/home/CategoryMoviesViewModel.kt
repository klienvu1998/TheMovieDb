package com.hyvu.themoviedb.viewmodel.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.rxjava2.cachedIn
import com.hyvu.themoviedb.data.remote.entity.MovieDetail
import com.hyvu.themoviedb.data.repository.MovieRepository
import com.hyvu.themoviedb.data.local.database.HomeDatabase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class CategoryMoviesViewModel @Inject constructor(val repository: MovieRepository): ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    @Inject
    lateinit var database: HomeDatabase

    private val _responseMovies: MutableLiveData<PagingData<MovieDetail>> = MutableLiveData()
    val responseMovies: LiveData<PagingData<MovieDetail>> = _responseMovies

    @ExperimentalPagingApi
    fun getMovieByGenre(genreId: Int) {
        compositeDisposable.add(
                repository.fetchMovieByGenre(genreId) {
                    database.homeMovieDetailDao().insertListMovieDetail(it)
                }.cachedIn(viewModelScope)
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            _responseMovies.postValue(it)
                        }, { e ->
                            e.printStackTrace()
                        })
        )
    }

    fun getMovieByGenreDatabase(genreId: Int) {
        compositeDisposable.add(
            database.homeMovieDetailDao().getMoviesByGenre(genreId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _responseMovies.postValue(PagingData.from(it))
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