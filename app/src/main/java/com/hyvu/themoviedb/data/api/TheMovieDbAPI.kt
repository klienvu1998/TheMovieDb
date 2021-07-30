package com.hyvu.themoviedb.data.api

import com.hyvu.themoviedb.data.entity.Genres
import com.hyvu.themoviedb.data.entity.MovieDetails
import com.hyvu.themoviedb.data.entity.MovieVideo
import com.hyvu.themoviedb.data.entity.MoviesByGenre
import io.reactivex.Observable
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
}