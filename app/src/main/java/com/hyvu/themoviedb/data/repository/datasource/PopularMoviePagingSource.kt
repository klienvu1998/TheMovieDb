package com.hyvu.themoviedb.data.repository.datasource

import androidx.paging.PagingState
import androidx.paging.rxjava2.RxPagingSource
import com.hyvu.themoviedb.data.api.TheMovieDbAPI
import com.hyvu.themoviedb.data.entity.MovieDetail
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class PopularMoviePagingSource(
        private val apiService: TheMovieDbAPI,
): RxPagingSource<Int, MovieDetail>() {
    override fun getRefreshKey(state: PagingState<Int, MovieDetail>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                    ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override fun loadSingle(params: LoadParams<Int>): Single<LoadResult<Int, MovieDetail>> {
        val position = params.key ?: 1
        return apiService.getPopularMovie(position)
                .subscribeOn(Schedulers.io())
                .map {  data ->
                    LoadResult.Page(
                            data = data.movieDetails,
                            nextKey = if (position == data.totalPages) null else position + 1,
                            prevKey = if (position == 1) null else position - 1
                    )
                }
    }

}