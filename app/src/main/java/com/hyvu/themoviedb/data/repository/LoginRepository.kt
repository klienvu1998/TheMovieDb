package com.hyvu.themoviedb.data.repository

import com.hyvu.themoviedb.data.remote.api.TheMovieDbAPI
import com.hyvu.themoviedb.di.scope.ActivityScope
import javax.inject.Inject

@ActivityScope
class LoginRepository @Inject constructor(val apiService: TheMovieDbAPI) {

}