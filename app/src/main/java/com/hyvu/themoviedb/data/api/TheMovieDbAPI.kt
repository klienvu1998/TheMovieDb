package com.hyvu.themoviedb.data.api

import com.hyvu.themoviedb.data.entity.*
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.*

interface TheMovieDbAPI {

    @GET("authentication/token/new")
    fun getAuthenticateToken(): Single<AuthenticateToken>

    @POST("authentication/session/new")
    fun getSessionID(
        @Body body: HashMap<String, Any>
    ): Single<Session>

    @GET("movie/popular")
    fun getPopularMovie(
            @Query("page") page: Int
    ): Single<MoviesByGenre>

    @GET("genre/movie/list")
    fun getListGenres(): Observable<Genres>

    @GET("discover/movie")
    fun getMoviesByGenre(
        @Query("with_genres") genes: Int,
        @Query("page") page: Int,
    ): Single<MoviesByGenre>

    @GET("movie/{movie_id}")
    fun getMovieDetails(
        @Path("movie_id")  movieId: Int
    ): Observable<MovieFullDetails>

    @GET("movie/{movie_id}/videos")
    fun getMovieVideos(
        @Path("movie_id") movieId: Int
    ): Observable<MovieVideos>

    @GET("trending/movie/day")
    fun getTrendingMovies(): Single<TrendingMovies>

    @GET("movie/latest")
    fun getLatestMovie(): Single<MovieDetail>

    @GET("movie/{movie_id}/credits")
    fun getCredits(
        @Path("movie_id") movieId: Int
    ): Single<Credits>

    @GET("movie/{movie_id}/reviews")
    fun getComments(
        @Path("movie_id") movieId: Int,
        @Query("page") page: Int
    ): Single<Comments>

    @GET("movie/{movie_id}/images")
    fun getMovieImages(
        @Path("movie_id") movieId: Int
    ): Single<MovieImages>
}