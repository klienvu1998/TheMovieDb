package com.hyvu.themoviedb.viewmodel.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.data.remote.api.TheMovieDbAPI
import com.hyvu.themoviedb.data.remote.entity.RequestTokenResponse
import com.hyvu.themoviedb.data.repository.AuthenticateRepository
import com.hyvu.themoviedb.data.local.database.HomeDatabase
import com.hyvu.themoviedb.data.local.entity.RequestToken
import com.hyvu.themoviedb.di.scope.ActivityScope
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@ActivityScope
class LoginViewModel @Inject constructor(private val authenticateRepository: AuthenticateRepository): ViewModel() {

    companion object {
        private const val TAG = "LoginViewModel"
    }

    private val compositeDisposable = CompositeDisposable()

    @Inject
    lateinit var database: HomeDatabase

    private val _viewEvent: MutableSharedFlow<LoginViewEvent> = MutableSharedFlow()
    val viewEvent: SharedFlow<LoginViewEvent>
        get() = _viewEvent

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    fun createGuestSession() {
        _isLoading.postValue(true)

        compositeDisposable.add(
            authenticateRepository.createGuestSession()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ session ->
                    _isLoading.postValue(false)
                    sendEvent(LoginViewEvent.ShowHomeScreen)
                }, { e ->
                    Log.e(TAG, e.message.toString())
                    _isLoading.postValue(false)
                    sendEvent(LoginViewEvent.ShowToast(R.string.normal_error))
                })
        )
    }

    fun sendEvent(event: LoginViewEvent) {
        viewModelScope.launch {
            _viewEvent.emit(event)
        }
    }

    fun createAuthenticateToken() {
        _isLoading.postValue(true)

        compositeDisposable.add(
            authenticateRepository.createRequestToken()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _isLoading.postValue(false)
                    sendEvent(LoginViewEvent.ShowWebLogin(it))
                }, { e ->
                    _isLoading.postValue(false)
                    sendEvent(LoginViewEvent.ShowToast(R.string.normal_error))
                })
        )
    }

    fun createUserSession(authenticateToken: String) {
        _isLoading.postValue(true)

        compositeDisposable.add(
            authenticateRepository.createUserSession(authenticateToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _isLoading.postValue(false)
                    sendEvent(LoginViewEvent.ShowHomeScreen)
                }, { e ->
                    _isLoading.postValue(false)
                    sendEvent(LoginViewEvent.ShowToast(R.string.normal_error))
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}

sealed class LoginViewEvent {
    data object ShowHomeScreen: LoginViewEvent()
    data class ShowToast(val stringId: Int): LoginViewEvent()
    data class ShowWebLogin(val authenticateToken: RequestToken): LoginViewEvent()
}