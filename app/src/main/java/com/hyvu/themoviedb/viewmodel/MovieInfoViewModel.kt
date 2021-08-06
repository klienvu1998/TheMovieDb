package com.hyvu.themoviedb.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.rxjava2.flowable
import com.hyvu.themoviedb.data.api.TheMovieDbClient
import com.hyvu.themoviedb.data.entity.*
import com.hyvu.themoviedb.data.repository.MovieRepository
import com.hyvu.themoviedb.data.repository.datasource.CommentPagingSource
import com.hyvu.themoviedb.utils.Constraints
import io.reactivex.Flowable
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