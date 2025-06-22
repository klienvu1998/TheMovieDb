package com.hyvu.themoviedb.viewmodel.splashscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.data.repository.AuthenticateRepository
import com.hyvu.themoviedb.di.scope.ActivityScope
import com.hyvu.themoviedb.viewmodel.login.LoginViewEvent
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@ActivityScope
class SplashScreenViewModel @Inject constructor(
    private val authenticateRepository: AuthenticateRepository
): ViewModel() {

    private val _viewEvent: MutableSharedFlow<SplashScreenViewEvent> = MutableSharedFlow(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val viewEvent: SharedFlow<SplashScreenViewEvent>
        get() = _viewEvent

    fun checkValidSession() {
        viewModelScope.launch {
            delay(50)
            val guestAuthen = authenticateRepository.getGuestSessionFromLocal()
            val userAuthen = authenticateRepository.getUserSessionFromLocal()
            if (guestAuthen != null) {
                if (guestAuthen.expiredAt < System.currentTimeMillis()) {
                    _viewEvent.emit(SplashScreenViewEvent.ShowHomeScreen)
                } else {
                    _viewEvent.emit(SplashScreenViewEvent.ShowToast(R.string.expire_session))
                    authenticateRepository.clearGuestSession()
                    _viewEvent.emit(SplashScreenViewEvent.ShowLoginScreen)
                }
            } else if (userAuthen != null) {
                _viewEvent.emit(SplashScreenViewEvent.ShowHomeScreen)
            } else {
                _viewEvent.emit(SplashScreenViewEvent.ShowLoginScreen)
            }
        }
    }

}

sealed class SplashScreenViewEvent {
    data object ShowHomeScreen: SplashScreenViewEvent()
    data object ShowLoginScreen: SplashScreenViewEvent()
    data class ShowToast(val stringId: Int): SplashScreenViewEvent()
}