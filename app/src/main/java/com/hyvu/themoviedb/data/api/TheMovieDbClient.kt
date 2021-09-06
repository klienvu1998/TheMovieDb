package com.hyvu.themoviedb.data.api

import com.hyvu.themoviedb.data.entity.AuthenticateToken
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

const val BASE_LOGIN_URL = "https://www.themoviedb.org/authenticate/"
const val API_KEY = "cf8254960d5832ecaae26e13929bb3b5"
const val BASE_URL = "https://api.themoviedb.org/3/"
const val BASE_IMG_LOW_QUALITY_URL = "https://image.tmdb.org/t/p/w300"
const val BASE_IMG_MEDIUM_QUALITY_URL = "https://image.tmdb.org/t/p/w780"
const val BASE_IMG_HIGH_QUALITY_URL = "https://image.tmdb.org/t/p/w1280"

object TheMovieDbClient {

    fun getAuthenticateDeepLink(authenticateToken: AuthenticateToken): String {
        return BASE_LOGIN_URL + authenticateToken.requestToken + "?redirect_to=http://www.themoviedb.com/approved"
    }

    fun getClient(): TheMovieDbAPI {

        val requestInterceptor = Interceptor { chain ->
            val url = chain.request()
                .url
                .newBuilder()
                .addQueryParameter("api_key", API_KEY)
                .build()

            val request = chain.request()
                .newBuilder()
                .url(url)
                .build()
            return@Interceptor chain.proceed(request)
        }

        val logInterceptor = HttpLoggingInterceptor()
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(requestInterceptor)
            .addInterceptor(logInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .build()
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TheMovieDbAPI::class.java)
    }

}