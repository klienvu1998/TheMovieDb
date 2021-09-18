package com.hyvu.themoviedb.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hyvu.themoviedb.data.entity.MoviesListResponse
import com.hyvu.themoviedb.data.repository.MovieRepository
import javax.inject.Inject

class UserViewModel @Inject constructor(repository: MovieRepository): ViewModel() {
    val favoriteList: LiveData<MoviesListResponse> = repository.favoriteList
}