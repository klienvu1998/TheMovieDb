package com.hyvu.themoviedb.di.module

import com.hyvu.themoviedb.data.api.API_KEY
import com.hyvu.themoviedb.data.api.BASE_URL
import com.hyvu.themoviedb.data.api.TheMovieDbAPI
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class RetrofitModule {

    @Singleton
    @Provides
    fun provideInterceptor(): Interceptor {
        return Interceptor { chain ->
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
    }

    @Singleton
    @Provides
    fun provideLogInterceptor(): HttpLoggingInterceptor {
        val logInterceptor = HttpLoggingInterceptor()
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return logInterceptor
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(requestInterceptor: Interceptor, logInterceptor: HttpLoggingInterceptor): OkHttpClient {
        val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(requestInterceptor)
                .addInterceptor(logInterceptor)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build()
        return okHttpClient
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

    }

    @Singleton
    @Provides
    fun provideApi(retrofit: Retrofit): TheMovieDbAPI {
        return retrofit.create(TheMovieDbAPI::class.java)
    }
}