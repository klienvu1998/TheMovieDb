package com.hyvu.themoviedb.data.entity


import com.google.gson.annotations.SerializedName

data class MoviesByGenre(
    @SerializedName("page")
    val page: Int? = null,
    @SerializedName("results")
    val movieOverviews: List<MovieOverview>? = null,
    @SerializedName("total_pages")
    val totalPages: Int? = null,
    @SerializedName("total_results")
    val totalResults: Int? = null
)