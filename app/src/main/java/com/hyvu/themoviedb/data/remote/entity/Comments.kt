package com.hyvu.themoviedb.data.remote.entity


import com.google.gson.annotations.SerializedName

data class Comments(
    @SerializedName("id")
    val id: Int,
    @SerializedName("page")
    val page: Int,
    @SerializedName("results")
    val comments: List<Comment>,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int
)