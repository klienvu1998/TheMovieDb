package com.hyvu.themoviedb.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hyvu.themoviedb.data.entity.AuthenticateToken
import com.hyvu.themoviedb.data.entity.Session
import com.hyvu.themoviedb.data.repository.LoginRepository
import javax.inject.Inject

class LoginViewModel @Inject constructor(private val loginRepository: LoginRepository): ViewModel() {

    val authenticateToken: LiveData<AuthenticateToken> = loginRepository.responseAuthenticateToken
    val session: LiveData<Session> = loginRepository.responseSession

    fun fetchAuthenticateToken() {
        loginRepository.fetchAuthenticateToken()
    }

    fun fetchSession(authenticateToken: String) {
        loginRepository.fetchSession(authenticateToken)
    }

}