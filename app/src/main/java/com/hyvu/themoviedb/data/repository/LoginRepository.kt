package com.hyvu.themoviedb.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hyvu.themoviedb.data.api.TheMovieDbAPI
import com.hyvu.themoviedb.data.entity.AuthenticateToken
import com.hyvu.themoviedb.data.entity.Session
import com.hyvu.themoviedb.di.scope.ActivityScope
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@ActivityScope
class LoginRepository @Inject constructor(val apiService: TheMovieDbAPI) {

    private val compositeDisposable = CompositeDisposable()
    private val _responseAuthenticateToken: MutableLiveData<AuthenticateToken> = MutableLiveData()
    val responseAuthenticateToken: LiveData<AuthenticateToken> = _responseAuthenticateToken
    private val _responseSession: MutableLiveData<Session> = MutableLiveData()
    val responseSession: LiveData<Session> = _responseSession

    fun fetchAuthenticateToken() {
        compositeDisposable.add(
            apiService.getAuthenticateToken()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _responseAuthenticateToken.postValue(it)
                }, { e ->
                    e.printStackTrace()
                })
        )
    }

    fun fetchSession(authenticateToken: String) {
        val body = HashMap<String, Any>()
        body["request_token"] = authenticateToken
            compositeDisposable.add(
            apiService.getSessionID(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _responseSession.postValue(it)
                }, { e ->
                    e.printStackTrace()
                })
        )
    }

    fun deinit() {
        compositeDisposable.clear()
    }

}