package com.hyvu.themoviedb.data.api

import com.hyvu.themoviedb.data.entity.*
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TheMovieDbAPI {

    @GET("movie/popular")
    fun getPopularMovie(
            @Query("page") page: Int)
    : Single<MoviesByGenre>

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
}