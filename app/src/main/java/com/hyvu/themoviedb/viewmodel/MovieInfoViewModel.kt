package com.hyvu.themoviedb.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.hyvu.themoviedb.data.api.TheMovieDbClient
import com.hyvu.themoviedb.data.entity.*
import com.hyvu.themoviedb.data.repository.MovieRepository
import com.hyvu.themoviedb.di.scope.ActivityScope
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@ActivityScope
class MovieInfoViewModel @Inject constructor(val repository: MovieRepository): ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    val movieVideos: LiveData<MovieVideos> = repository.responseMovieVideos
    val movieCredits: LiveData<Credits> = repository.responseMovieCredits
    val movieFullDetails: LiveData<MovieFullDetails> = repository.responseCurrentMovieDetail
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

    fun fetchMovieDetails(movieId: Int) {
        repository.fetchMovieDetails(movieId)
    }

    fun fetchMovieCredits(movieId: Int) {
        repository.fetchMovieCredits(movieId)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}