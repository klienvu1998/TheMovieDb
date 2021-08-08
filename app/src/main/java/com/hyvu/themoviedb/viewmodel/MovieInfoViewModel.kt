package com.hyvu.themoviedb.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.hyvu.themoviedb.data.api.TheMovieDbClient
import com.hyvu.themoviedb.data.entity.*
import com.hyvu.themoviedb.data.repository.MovieRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MovieInfoViewModel: ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    val movieVideos: LiveData<MovieVideos> = MovieRepository.responseMovieVideos
    val movieCredits: LiveData<Credits> = MovieRepository.responseMovieCredits
    val movieFullDetails: LiveData<MovieFullDetails> = MovieRepository.responseCurrentMovieDetail
    private val _movieComments: MutableLiveData<PagingData<Comment>> = MutableLiveData()
    val movieComments: LiveData<PagingData<Comment>> = _movieComments
    private val _movieImages: MutableLiveData<MovieImages> = MutableLiveData()
    val movieImages: LiveData<MovieImages> = _movieImages

    fun fetchMovieImages(movieId: Int) {
        compositeDisposable.add(
            TheMovieDbClient.getClient().getMovieImages(movieId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _movieImages.postValue(it)
                }, { e ->
                    e.printStackTrace()
                })
        )
    }

    fun fetchMovieComments(movieId: Int) {
        compositeDisposable.add(
            MovieRepository.fetchMovieComments(movieId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _movieComments.postValue(it)
                }, { e ->
                    e.printStackTrace()
                })
        )
    }

    fun fetchMovieDetails(movieId: Int) {
        MovieRepository.fetchMovieDetails(movieId)
    }

    fun fetchMovieCredits(movieId: Int) {
        MovieRepository.fetchMovieCredits(movieId)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}