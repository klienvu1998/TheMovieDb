package com.hyvu.themoviedb.data.remote.datasource

import android.util.Log
import com.hyvu.themoviedb.data.remote.api.TheMovieDbAPI
import com.hyvu.themoviedb.data.remote.entity.GuestSessionResponse
import com.hyvu.themoviedb.data.remote.entity.RequestTokenResponse
import com.hyvu.themoviedb.data.remote.entity.UserSessionResponse
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class AuthenticateRemoteDataSourceImpl @Inject constructor(val api: TheMovieDbAPI): AuthenticateRemoteDataSource {

    companion object {
        private const val TAG = "RemoteAuthenticateRemoteDataSourceImpl"
    }

    override fun createRequestToken(): Single<RequestTokenResponse> {
        return api.createRequestToken()
            .subscribeOn(Schedulers.io())
            .doOnError { throwable ->
                Log.e(TAG, throwable.message.toString())
            }
            .onErrorResumeNext {
                Single.error(CreateRequestTokenException(it.message.toString()))
            }
    }

    override fun createGuestSession(): Single<GuestSessionResponse> {
        return api.createGuestSession()
            .subscribeOn(Schedulers.io())
            .doOnError { throwable ->
                Log.e(TAG, throwable.message.toString())
            }
            .onErrorResumeNext {
                Single.error(GuestSessionCreationException(it.message.toString()))
            }
    }

    override fun createUserSession(authenToken: String): Single<UserSessionResponse> {
        val body = HashMap<String, Any>()
        body["request_token"] = authenToken

        return api.createUserSession(body)
            .subscribeOn(Schedulers.io())
            .doOnError { throwable ->
                Log.e(TAG, throwable.message.toString())
            }
            .onErrorResumeNext {
                Single.error(UserSessionCreationException(it.message.toString()))
            }
    }
}

class CreateRequestTokenException(message: String): Exception(message)
class GuestSessionCreationException(message: String) : Exception(message)
class UserSessionCreationException(message: String): Exception(message)
