package com.hyvu.themoviedb.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

    private val _favorite: MutableLiveData<BaseResponse> = MutableLiveData()
    val favorite: LiveData<BaseResponse> = _favorite

    private val _watchListResponse: MutableLiveData<BaseResponse> = MutableLiveData()
    val watchListResponse: LiveData<BaseResponse> = _watchListResponse

    val favoriteList: LiveData<MoviesListResponse> = repository.favoriteList
    val watchList: LiveData<MoviesListResponse> = repository.watchList

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

    fun setFavorite(accountId: Int, sessionId: String, movieId: Int, isFavorite: Boolean) {
        val body = HashMap<String, Any>()
        body["media_type"] = "movie"
        body["media_id"] = movieId
        body["favorite"] = isFavorite
        compositeDisposable.add(
            TheMovieDbClient.getClient().setFavorite(accountId, sessionId, body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _favorite.postValue(it)
                }, { e ->
                    e.printStackTrace()
                })
        )
    }

    fun addWatchList(accountId: Int, sessionId: String, movieId: Int, isWatchedList: Boolean) {
        val body = HashMap<String, Any>()
        body["media_type"] = "movie"
        body["media_id"] = movieId
        body["watchlist"] = isWatchedList
        compositeDisposable.add(
            TheMovieDbClient.getClient().addWatchList(accountId, sessionId, body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _watchListResponse.postValue(it)
                }, { e ->
                    e.printStackTrace()
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    fun updateFavoriteList(movieDetail: MovieDetail) {
        repository.updateFavoriteList(movieDetail)
    }

    fun updateWatchList(movieDetail: MovieDetail) {
        repository.updateWatchList(movieDetail)
    }
}