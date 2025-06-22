package com.hyvu.themoviedb.viewmodel.home

import androidx.lifecycle.ViewModel
import com.hyvu.themoviedb.data.repository.AuthenticateRepository
import com.hyvu.themoviedb.data.repository.MovieRepository
import javax.inject.Inject

class UserViewModel @Inject constructor(private val authenticateRepository: AuthenticateRepository, private val movieRepository: MovieRepository): ViewModel() {

    fun isLoggedIn(): Boolean {
        return authenticateRepository.getUserSessionFromLocal() != null
    }

}