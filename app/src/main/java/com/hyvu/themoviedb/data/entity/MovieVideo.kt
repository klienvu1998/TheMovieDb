package com.hyvu.themoviedb.data.entity


import com.google.gson.annotations.SerializedName

data class MovieVideo(
    @SerializedName("id")
    val id: Int,
    @SerializedName("results")
    val movieVideoDetails: List<MovieVideoDetail>
)