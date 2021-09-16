package com.hyvu.themoviedb.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hyvu.themoviedb.data.entity.Genre
import com.hyvu.themoviedb.data.entity.MovieDetail
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow

@Dao
interface HomeMovieDetailDao {

    @Query("SELECT * FROM movie_detail")
    fun getListMovieDetail(): Single<List<MovieDetail>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMovieDetailToDatabase(movieDetail: MovieDetail)

    @Query("SELECT * FROM genre")
    fun getListGenres(): Single<List<Genre>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertGenreToDatabase(genre: Genre)

}