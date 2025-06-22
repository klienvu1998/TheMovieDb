package com.hyvu.themoviedb.viewmodel.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyvu.themoviedb.data.repository.AuthenticateRepository
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingViewModel @Inject constructor(val authenticateRepository: AuthenticateRepository): ViewModel() {

    private val _viewEvent: MutableSharedFlow<SettingViewEvent> = MutableSharedFlow(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val viewEvent: SharedFlow<SettingViewEvent>
        get() = _viewEvent

    fun isLoggedIn(): Boolean {
        return authenticateRepository.getUserSessionFromLocal() != null
    }

    fun onLoggedOut() {
        authenticateRepository.clearUserSession()
        authenticateRepository.clearGuestSession()
        viewModelScope.launch {
            _viewEvent.emit(SettingViewEvent.ShowLoginScreen)
        }
    }

}

sealed class SettingViewEvent {
    data object ShowLoginScreen: SettingViewEvent()
}