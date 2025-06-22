package com.hyvu.themoviedb.data.repository

import com.hyvu.themoviedb.data.local.datasource.AuthenticateLocalDataSource
import com.hyvu.themoviedb.data.local.entity.GuestSession
import com.hyvu.themoviedb.data.local.entity.RequestToken
import com.hyvu.themoviedb.data.local.entity.UserSession
import com.hyvu.themoviedb.data.remote.datasource.AuthenticateRemoteDataSource
import com.hyvu.themoviedb.data.remote.datasource.CreateRequestTokenException
import com.hyvu.themoviedb.data.remote.datasource.GuestSessionCreationException
import com.hyvu.themoviedb.data.remote.datasource.UserSessionCreationException
import com.hyvu.themoviedb.di.scope.ActivityScope
import io.reactivex.Single
import javax.inject.Inject

@ActivityScope
class AuthenticateRepository @Inject constructor(
    private val remoteAuthenticateDataSource: AuthenticateRemoteDataSource,
    private val localAuthenticateDataSource: AuthenticateLocalDataSource
) {

    fun createRequestToken(): Single<RequestToken> {
        return remoteAuthenticateDataSource.createRequestToken()
            .flatMap { res ->
                if (res.success) {
                    val requestToken = RequestToken.mapData(res)
                    Single.just(requestToken)
                } else {
                    Single.error(CreateRequestTokenException("Req failed"))
                }
            }
    }

    fun createGuestSession(): Single<GuestSession> {
        return remoteAuthenticateDataSource.createGuestSession()
            .flatMap { res ->
                val guestSession = GuestSession.mapData(res)

                if (res.success && guestSession != null) {
                    localAuthenticateDataSource.saveGuestSession(guestSession)
                    Single.just(guestSession)
                } else {
                    Single.error(GuestSessionCreationException("Null data"))
                }
            }
    }

    fun getGuestSessionFromLocal(): GuestSession? {
        return localAuthenticateDataSource.getGuestSession()
    }

    fun clearGuestSession() {
        localAuthenticateDataSource.clearGuestSession()
    }

    fun createUserSession(authenToken: String): Single<UserSession> {
        return remoteAuthenticateDataSource.createUserSession(authenToken)
            .flatMap { res ->
                val userSession = UserSession.mapData(res)
                if (res.success && userSession != null) {
                    localAuthenticateDataSource.saveUserSession(userSession)
                    Single.just(userSession)
                } else {
                    Single.error(UserSessionCreationException("Null data"))
                }
            }
    }

    fun getUserSessionFromLocal(): UserSession? {
        return localAuthenticateDataSource.getUserSession()
    }

    fun clearUserSession() {
        localAuthenticateDataSource.clearUserSession()
    }

}