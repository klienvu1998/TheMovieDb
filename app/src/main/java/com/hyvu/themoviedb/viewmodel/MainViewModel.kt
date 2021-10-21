package com.hyvu.themoviedb.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hyvu.themoviedb.data.entity.*
import com.hyvu.themoviedb.data.repository.MovieRepository
import com.hyvu.themoviedb.di.scope.ActivityScope
import javax.inject.Inject

@ActivityScope
class MainViewModel @Inject constructor(val repository: MovieRepository): ViewModel() {

    val movieVideos: LiveData<MovieVideos> = repository.responseMovieVideos
    val accountDetails: LiveData<AccountDetails> = repository.accountDetails

    override fun onCleared() {
        super.onCleared()
        repository.deinit()
    }

    fun fetchFavoriteMovie(accountId: Int, sessionId: String) {
        repository.fetchFavoriteMovie(accountId, sessionId)
    }

    fun fetchAccountDetail(sessionId: String) {
        repository.fetchAccountDetail(sessionId)
    }

    fun fetchWatchList(accountId: Int, sessionId: String) {
        repository.fetchWatchList(accountId, sessionId)
    }
}