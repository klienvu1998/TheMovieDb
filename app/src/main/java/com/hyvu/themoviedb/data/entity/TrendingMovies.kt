package com.hyvu.themoviedb.data.entity


import com.google.gson.annotations.SerializedName

data class TrendingMovies(
        @SerializedName("page")
    val page: Int,
        @SerializedName("results")
    val trendingMovies: List<TrendingMovie>,
        @SerializedName("total_pages")
    val totalPages: Int,
        @SerializedName("total_results")
    val totalResults: Int
)