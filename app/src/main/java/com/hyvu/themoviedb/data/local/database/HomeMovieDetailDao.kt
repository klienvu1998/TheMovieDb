package com.hyvu.themoviedb.data.local.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hyvu.themoviedb.data.remote.entity.Genre
import com.hyvu.themoviedb.data.remote.entity.MovieDetail
import io.reactivex.Single

@Dao
interface HomeMovieDetailDao {

    @Query("SELECT * FROM genre ORDER BY name ASC")
    fun getListGenres(): Single<List<Genre>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertListGenres(genre: List<Genre>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertListMovieDetail(movieData: List<MovieDetail>)

    @Query("DELETE FROM movie_detail")
    fun clearMovies()

    @Query("SELECT * FROM movie_detail WHERE genre_ids LIKE '%' || :genreId || '%' ORDER BY movie_id DESC")
    fun getPagingListMovieDetailByGenre(genreId: Int): PagingSource<Int, MovieDetail>

    @Query("SELECT * FROM movie_detail WHERE genre_ids LIKE '%' || :genreId || '%' ORDER BY movie_id DESC")
    fun getMoviesByGenre(genreId: Int): Single<List<MovieDetail>>
}