package com.hyvu.themoviedb.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.rxjava2.flowable
import com.hyvu.themoviedb.data.remote.api.TheMovieDbAPI
import com.hyvu.themoviedb.data.remote.entity.*
import com.hyvu.themoviedb.data.repository.datasource.CommentPagingSource
import com.hyvu.themoviedb.data.repository.datasource.MoviePagingSource
import com.hyvu.themoviedb.data.repository.datasource.PopularMoviePagingSource
import com.hyvu.themoviedb.data.local.database.HomeDatabase
import com.hyvu.themoviedb.utils.Constraints
import io.reactivex.Flowable
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.ArrayList

@Singleton
class MovieRepository @Inject constructor(private val apiService: TheMovieDbAPI, private val database: HomeDatabase) {

    var mapHomeMovies: LinkedHashMap<Genre, List<MovieDetail>> = LinkedHashMap()
    var favoriteList: ArrayList<MovieDetail> = ArrayList()
    var watchList: ArrayList<MovieDetail> = ArrayList()
    var currentMovieFullDetail: MovieFullDetails? = null
    var currentMovieDetail: MovieDetail? = null
    var genres: ArrayList<Genre> = ArrayList()

    fun fetchMovieByGenre(genreId: Int, onGetMovieDetails: (List<MovieDetail>) -> Unit): Flowable<PagingData<MovieDetail>> {
        return Pager(
            config = PagingConfig(
                maxSize = Constraints.MAX_ITEM_PER_SCROLL,
                pageSize = Constraints.NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { MoviePagingSource(apiService, genreId, onGetMovieDetails) }
        ).flowable
    }

    fun fetchPopularMovies(): Flowable<PagingData<MovieDetail>> {
        return Pager(
                config = PagingConfig(
                        maxSize =  Constraints.MAX_ITEM_PER_SCROLL,
                        pageSize = Constraints.NETWORK_PAGE_SIZE,
                        enablePlaceholders = false
                ),
                pagingSourceFactory = { PopularMoviePagingSource(apiService) }
        ).flowable
    }

    fun fetchMovieComments(movieId: Int): Flowable<PagingData<Comment>> {
        return Pager(
            config = PagingConfig(
                maxSize = Constraints.MAX_ITEM_PER_SCROLL,
                pageSize = Constraints.NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { CommentPagingSource(apiService, movieId) }
        ).flowable
    }

    fun updateFavoriteList(movieDetail: MovieDetail) {
        if (movieDetail.isFavorite) {
            favoriteList.add(movieDetail)
        }
        else {
            favoriteList.remove(movieDetail)
        }
    }

    fun updateWatchList(movieDetail: MovieDetail) {
        if (movieDetail.isWatchList) {
            watchList.add(movieDetail)
        }
        else {
            watchList.remove(movieDetail)
        }
    }

}