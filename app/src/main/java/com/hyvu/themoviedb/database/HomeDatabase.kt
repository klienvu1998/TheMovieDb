package com.hyvu.themoviedb.database

import android.content.Context
import androidx.room.*
import com.hyvu.themoviedb.data.entity.Genre
import com.hyvu.themoviedb.data.entity.MovieDetail

@Database(entities = arrayOf(MovieDetail::class, Genre::class), version = 1, exportSchema = false)
@ProvidedTypeConverter
abstract class HomeDatabase: RoomDatabase() {

    abstract fun homeMovieDetailDao(): HomeMovieDetailDao

    companion object {
        @Volatile
        private var instance: HomeDatabase? = null
        fun getDatabase(context: Context): HomeDatabase {
            return instance ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HomeDatabase::class.java,
                    "movie_detail"
                ).build()
                this.instance = instance
                instance
            }
        }
    }

}