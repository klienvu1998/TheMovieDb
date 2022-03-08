package com.hyvu.themoviedb.viewmodel.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hyvu.themoviedb.data.remote.api.TheMovieDbAPI
import com.hyvu.themoviedb.data.remote.entity.*
import com.hyvu.themoviedb.data.repository.MovieRepository
import com.hyvu.themoviedb.data.local.database.HomeDatabase
import com.hyvu.themoviedb.di.scope.ActivityScope
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@ActivityScope
class MainViewModel @Inject constructor(val repository: MovieRepository): ViewModel() {

    private val _accountDetails: MutableLiveData<AccountDetails> = MutableLiveData()
    val accountDetails: LiveData<AccountDetails> = _accountDetails

    var currentMovie = repository.currentMovieDetail

    private val compositeDisposable = CompositeDisposable()
    @Inject
    lateinit var apiService: TheMovieDbAPI

    @Inject
    lateinit var database: HomeDatabase

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    fun setCurrentMovieDetail(movieDetail: MovieDetail) {
        repository.currentMovieDetail = movieDetail
    }

    fun fetchAccountDetail(sessionId: String) {
        compositeDisposable.add(
            apiService.getAccountDetail(sessionId)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    _accountDetails.postValue(it)
                }, { e ->
                    e.printStackTrace()
                })
        )
    }
}