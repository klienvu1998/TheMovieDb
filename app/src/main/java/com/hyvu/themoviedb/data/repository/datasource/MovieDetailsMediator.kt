package com.hyvu.themoviedb.data.repository.datasource

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.rxjava2.RxRemoteMediator
import com.hyvu.themoviedb.data.api.TheMovieDbAPI
import com.hyvu.themoviedb.data.entity.MovieDetail
import com.hyvu.themoviedb.data.entity.MoviesListResponse
import com.hyvu.themoviedb.data.entity.MovieDetailRemoteKey
import com.hyvu.themoviedb.database.HomeDatabase
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.io.InvalidObjectException

@ExperimentalPagingApi
class MovieDetailsMediator(
    private val genreId: Int,
    private val apiService: TheMovieDbAPI,
    private val database: HomeDatabase
): RxRemoteMediator<Int, MovieDetail>() {

    companion object {
        const val INVALID_PAGE = -1
    }

    override fun loadSingle(
        loadType: LoadType,
        state: PagingState<Int, MovieDetail>
    ): Single<MediatorResult> {
        return Single.just(loadType)
            .subscribeOn(Schedulers.io())
            .map {
                when (it) {
                    LoadType.REFRESH -> {
                        val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                        remoteKeys?.nextKey?.minus(1) ?: 1
                    }
                    LoadType.PREPEND -> {
                        val remoteKeys = getRemoteKeyForFirstItem(state) ?: throw InvalidObjectException("Result is Empty")
                        remoteKeys.prevKey ?: INVALID_PAGE
                    }
                    LoadType.APPEND -> {
                        val remoteKeys = getRemoteKeyForLastItem(state) ?: throw InvalidObjectException("Result is Empty")
                        remoteKeys.nextKey ?: INVALID_PAGE
                    }
                }
            }.flatMap { page ->
                if (page == INVALID_PAGE) {
                    Single.just(MediatorResult.Success(endOfPaginationReached = false))
                } else {
                    apiService.getMoviesByGenre(genreId, page)
                        .map { movie -> insertToDb(page, loadType, movie) }
                        .map<MediatorResult> {  MediatorResult.Success(endOfPaginationReached = (false)) }
                        .onErrorReturn { MediatorResult.Error(it) }
                }
            }
            .onErrorReturn { MediatorResult.Error(it) }
    }

    private fun insertToDb(page: Int, loadType: LoadType, data: MoviesListResponse): MoviesListResponse {
        database.runInTransaction {
            if (loadType == LoadType.REFRESH) {
                database.remoteKeysDao().clearRemoteKeys()
                database.homeMovieDetailDao().clearMovies()
            }
            val prevKey = if (page == 1) null else page - 1
            val nextKey = if (data.endOfPage()) null else page + 1
            val keys = data.movieDetails.map {
                MovieDetailRemoteKey(movieId = it.movieId, prevKey = prevKey, nextKey = nextKey)
            }
            database.remoteKeysDao().insertAll(keys)
            database.homeMovieDetailDao().insertListMovieDetail(data.movieDetails)
        }
        return data
    }

    private fun getRemoteKeyForLastItem(state: PagingState<Int, MovieDetail>): MovieDetailRemoteKey? {
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { repo ->
                // Get the remote keys of the last item retrieved
                database.remoteKeysDao().remoteKeyMovieId(repo.movieId)
            }
    }

    private fun getRemoteKeyForFirstItem(state: PagingState<Int, MovieDetail>): MovieDetailRemoteKey? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { repo ->
                // Get the remote keys of the first items retrieved
                database.remoteKeysDao().remoteKeyMovieId(repo.movieId)
            }
    }


    private fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, MovieDetail>): MovieDetailRemoteKey? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.movieId?.let { repoId ->
                database.remoteKeysDao().remoteKeyMovieId(repoId)
            }
        }
    }

}