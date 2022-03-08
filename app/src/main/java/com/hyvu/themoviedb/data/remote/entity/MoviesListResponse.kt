package com.hyvu.themoviedb.data.remote.entity


import com.google.gson.annotations.SerializedName

data class MoviesListResponse(
        @SerializedName("page")
    val page: Int? = null,
        @SerializedName("results")
    val movieDetails: List<MovieDetail>,
        @SerializedName("total_pages")
    val totalPages: Int? = null,
        @SerializedName("total_results")
    val totalResults: Int? = null
) {
    fun endOfPage(): Boolean {
        return this.page == this.totalPages
    }
}