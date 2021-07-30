package com.hyvu.themoviedb.data.api

import com.hyvu.themoviedb.data.entity.*
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TheMovieDbAPI {

    @GET("movie/popular")
    fun getPopularMovie(): Observable<MoviesByGenre>

    @GET("genre/movie/list")
    fun getListGenres(): Observable<Genres>

    @GET("discover/movie")
    fun getListMovieOverview(
        @Query("with_genres") genes: Int
    ): Observable<MoviesByGenre>

    @GET("movie/{movie_id}")
    fun getMovieDetails(
        @Path("movie_id")  movieId: Int
    ): Observable<MovieDetails>

    @GET("movie/{movie_id}/videos")
    fun getMovieVideos(
        @Path("movie_id") movieId: Int
    ): Observable<MovieVideo>

    @GET("trending/movie/day")
    fun getTrendingMovies(): Single<TrendingMovies>
}