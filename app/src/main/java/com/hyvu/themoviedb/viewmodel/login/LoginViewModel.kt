package com.hyvu.themoviedb.viewmodel.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hyvu.themoviedb.data.remote.api.TheMovieDbAPI
import com.hyvu.themoviedb.data.remote.entity.AuthenticateToken
import com.hyvu.themoviedb.data.remote.entity.Session
import com.hyvu.themoviedb.data.repository.LoginRepository
import com.hyvu.themoviedb.data.local.database.HomeDatabase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class LoginViewModel @Inject constructor(private val loginRepository: LoginRepository): ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    @Inject
    lateinit var apiService: TheMovieDbAPI

    @Inject
    lateinit var database: HomeDatabase

    private val _authenticateToken: MutableLiveData<AuthenticateToken> = MutableLiveData()
    val authenticateToken: LiveData<AuthenticateToken> = _authenticateToken
    private val _session: MutableLiveData<Session> = MutableLiveData()
    val session: LiveData<Session> = _session

    fun fetchAuthenticateToken() {
        compositeDisposable.add(
            apiService.getAuthenticateToken()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _authenticateToken.postValue(it)
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
                    _session.postValue(it)
                }, { e ->
                    e.printStackTrace()
                })
        )
    }



}