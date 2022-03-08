package com.hyvu.themoviedb.viewmodel.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hyvu.themoviedb.data.remote.api.TheMovieDbAPI
import com.hyvu.themoviedb.data.remote.api.TheMovieDbClient
import com.hyvu.themoviedb.data.remote.entity.MovieDetail
import com.hyvu.themoviedb.data.remote.entity.MovieFullDetails
import com.hyvu.themoviedb.data.remote.entity.MovieVideos
import com.hyvu.themoviedb.data.repository.MovieRepository
import com.hyvu.themoviedb.data.local.database.HomeDatabase
import com.hyvu.themoviedb.di.scope.ActivityScope
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@ActivityScope
class SharedViewModel @Inject constructor(val repository: MovieRepository): ViewModel() {

    enum class STATUS {
        SUCCESS, FAIL, UNKNOWN
    }

    private val compositeDisposable = CompositeDisposable()

    @Inject
    lateinit var apiService: TheMovieDbAPI

    @Inject
    lateinit var database: HomeDatabase

    private val _initComplete: MutableLiveData<STATUS> = MutableLiveData(STATUS.UNKNOWN)
    val initComplete: LiveData<STATUS> = _initComplete

    private val _favoriteList: MutableLiveData<ArrayList<MovieDetail>> = MutableLiveData(repository.favoriteList)
    val favoriteList: LiveData<ArrayList<MovieDetail>> = _favoriteList

    private val _isFavoriteSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val isFavoriteSuccess: LiveData<Boolean> = _isFavoriteSuccess

    private val _isWatchListSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val isWatchListSuccess: LiveData<Boolean> = _isWatchListSuccess

    private val _watchList: MutableLiveData<ArrayList<MovieDetail>> = MutableLiveData(repository.watchList)
    val watchList: LiveData<ArrayList<MovieDetail>> = _watchList

    private val _currentMovieDetail = MutableLiveData<MovieFullDetails>()
    val currentMovieDetail: LiveData<MovieFullDetails> = _currentMovieDetail

    private val _movieVideos = MutableLiveData<MovieVideos>()
    val movieVideos: LiveData<MovieVideos> = _movieVideos

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
                    _isFavoriteSuccess.postValue(it.success)
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
                    _isWatchListSuccess.postValue(it.success)
                }, { e ->
                    e.printStackTrace()
                })
        )
    }

    fun updateFavoriteList(movieDetail: MovieDetail) {
        repository.updateFavoriteList(movieDetail)
        _favoriteList.postValue(repository.favoriteList)
    }

    fun updateWatchList(movieDetail: MovieDetail) {
        repository.updateWatchList(movieDetail)
        _watchList.postValue(repository.watchList)
    }

    fun fetchMovieDetails(movieId: Int) {
        compositeDisposable.add(
            apiService.getMovieDetails(movieId)
                .flatMap { detail ->
                    _currentMovieDetail.postValue(detail)
                    repository.currentMovieFullDetail = detail
                    apiService.getMovieVideos(movieId)
                }
                .subscribeOn(Schedulers.io())
                .subscribe({ videos ->
                    _movieVideos.postValue(videos)
                }, { e ->
                    e.printStackTrace()
                })
        )
    }

    fun fetchUserData(accountId: Int, sessionId: String) {
        compositeDisposable.add(
            apiService.getFavoriteMovie(accountId, sessionId)
                .flatMap {
                    repository.favoriteList = it.movieDetails as ArrayList<MovieDetail>
                    _favoriteList.postValue(it.movieDetails)
                    apiService.getWatchList(accountId, sessionId)
                }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    repository.watchList = it.movieDetails as ArrayList<MovieDetail>
                    _watchList.postValue(it.movieDetails)
                }, { e ->
                    e.printStackTrace()
                    _initComplete.postValue(STATUS.FAIL)
                }, {
                    _initComplete.postValue(STATUS.SUCCESS)
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}