package com.hyvu.themoviedb.data.remote.datasource

import com.hyvu.themoviedb.data.remote.entity.GuestSessionResponse
import com.hyvu.themoviedb.data.remote.entity.RequestTokenResponse
import com.hyvu.themoviedb.data.remote.entity.UserSessionResponse
import io.reactivex.Single

interface AuthenticateRemoteDataSource {

    fun createGuestSession(): Single<GuestSessionResponse>

    fun createUserSession(authenToken: String): Single<UserSessionResponse>

    fun createRequestToken(): Single<RequestTokenResponse>

}