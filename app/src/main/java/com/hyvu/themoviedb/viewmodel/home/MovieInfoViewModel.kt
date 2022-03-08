package com.hyvu.themoviedb.viewmodel.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hyvu.themoviedb.data.remote.api.TheMovieDbAPI
import com.hyvu.themoviedb.data.remote.entity.*
import com.hyvu.themoviedb.data.repository.MovieRepository
import com.hyvu.themoviedb.data.local.database.HomeDatabase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MovieInfoViewModel @Inject constructor(val repository: MovieRepository): ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    @Inject
    lateinit var apiService: TheMovieDbAPI

    @Inject
    lateinit var database: HomeDatabase

    private val _movieCredits: MutableLiveData<Credits> = MutableLiveData()
    val movieCredits: LiveData<Credits> = _movieCredits

    private val _movieImages: MutableLiveData<MovieImages> = MutableLiveData()
    val movieImages: LiveData<MovieImages> = _movieImages

    val favoriteList: ArrayList<MovieDetail> = repository.favoriteList
    val watchList: ArrayList<MovieDetail> = repository.watchList

    val currentMovie = repository.currentMovieDetail

    fun fetchMovieImages(movieId: Int) {
        compositeDisposable.add(
            apiService.getMovieImages(movieId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _movieImages.postValue(it)
                }, { e ->
                    e.printStackTrace()
                })
        )
    }

    fun fetchMovieCredits(movieId: Int) {
        compositeDisposable.add(
            apiService.getCredits(movieId)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    _movieCredits.postValue(it)
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