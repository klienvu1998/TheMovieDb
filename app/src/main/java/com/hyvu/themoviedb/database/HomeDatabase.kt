package com.hyvu.themoviedb.database

import android.content.Context
import androidx.room.*
import com.hyvu.themoviedb.data.entity.Genre
import com.hyvu.themoviedb.data.entity.Genres
import com.hyvu.themoviedb.data.entity.MovieDetail
import com.hyvu.themoviedb.data.entity.MovieDetailRemoteKey
import com.hyvu.themoviedb.utils.Constraints

@Database(entities = [MovieDetail::class, Genre::class, MovieDetailRemoteKey::class], version = 1, exportSchema = false)
@ProvidedTypeConverter
abstract class HomeDatabase: RoomDatabase() {

    abstract fun homeMovieDetailDao(): HomeMovieDetailDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        @Volatile
        private var instance: HomeDatabase? = null
        fun getDatabase(context: Context): HomeDatabase {
            return instance ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HomeDatabase::class.java,
                    Constraints.DATABASE_NAME
                ).build()
                this.instance = instance
                instance
            }
        }
    }

}