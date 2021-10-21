package com.hyvu.themoviedb.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hyvu.themoviedb.data.entity.MovieDetailRemoteKey

@Dao
interface RemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(movieDetailRemoteKey: List<MovieDetailRemoteKey>)

    @Query("SELECT * FROM remote_keys WHERE movie_id = :movieId")
    fun remoteKeyMovieId(movieId: Int): MovieDetailRemoteKey?

    @Query("DELETE FROM remote_keys")
    fun clearRemoteKeys()
}