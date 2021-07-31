package com.hyvu.themoviedb.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.hyvu.themoviedb.data.entity.MovieDetail
import com.hyvu.themoviedb.data.entity.MoviesByGenre
import com.hyvu.themoviedb.data.entity.TikMovie
import com.hyvu.themoviedb.data.repository.MovieRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class TikMovieViewModel: ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    private val _tikMovieDetails: MutableLiveData<PagingData<MovieDetail>> = MutableLiveData()
    val tikMovieDetails: LiveData<PagingData<MovieDetail>> = _tikMovieDetails

    fun fetchLatestMovie() {
        MovieRepository.fetchLatestMovie()
    }

    fun fetchTikMovie() {
        compositeDisposable.add(
                MovieRepository.fetchPopularMovies()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ data ->
                            _tikMovieDetails.postValue(data)
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