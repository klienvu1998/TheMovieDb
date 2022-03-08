package com.hyvu.themoviedb.data.repository.datasource

import androidx.paging.PagingState
import androidx.paging.rxjava2.RxPagingSource
import com.hyvu.themoviedb.data.remote.api.TheMovieDbAPI
import com.hyvu.themoviedb.data.remote.entity.MovieDetail
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class MoviePagingSource(
    private val apiService: TheMovieDbAPI,
    private val genreId: Int,
    private val onGetMovieDetails: (List<MovieDetail>) -> Unit
): RxPagingSource<Int, MovieDetail>() {

    companion object {
        const val MOVIE_PAGE_INDEX = 1
    }

    override fun getRefreshKey(state: PagingState<Int, MovieDetail>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override fun loadSingle(params: LoadParams<Int>): Single<LoadResult<Int, MovieDetail>> {
        val position = params.key ?: MOVIE_PAGE_INDEX
        return apiService.getMoviesByGenre(genreId, position)
            .subscribeOn(Schedulers.io())
            .map { data ->
                onGetMovieDetails.invoke(data.movieDetails)
                LoadResult.Page(
                    data = data.movieDetails,
                    prevKey = if (position == 1) null else position-1,
                    nextKey = if (position == data.totalPages) null else position + 1
                )
            }
    }


}