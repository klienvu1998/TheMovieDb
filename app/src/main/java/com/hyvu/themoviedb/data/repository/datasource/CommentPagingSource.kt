package com.hyvu.themoviedb.data.repository.datasource

import androidx.paging.PagingState
import androidx.paging.rxjava2.RxPagingSource
import com.hyvu.themoviedb.data.remote.api.TheMovieDbAPI
import com.hyvu.themoviedb.data.remote.entity.Comment
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class CommentPagingSource(
    private val apiService: TheMovieDbAPI,
    private val movieId: Int,
): RxPagingSource<Int, Comment>() {
    override fun getRefreshKey(state: PagingState<Int, Comment>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override fun loadSingle(params: LoadParams<Int>): Single<LoadResult<Int, Comment>> {
        val position = params.key ?: 1
        return apiService.getComments(movieId, position)
            .subscribeOn(Schedulers.io())
            .map { data ->
                LoadResult.Page(
                    data = data.comments,
                    nextKey = if (position == data.totalPages) null else position + 1,
                    prevKey = if (position == 1) null else position - 1
                )
            }
    }
}