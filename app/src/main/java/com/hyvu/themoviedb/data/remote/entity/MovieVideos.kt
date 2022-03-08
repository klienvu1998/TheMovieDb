package com.hyvu.themoviedb.data.remote.entity


import com.google.gson.annotations.SerializedName

data class MovieVideos(
    @SerializedName("id")
    val id: Int,
    @SerializedName("results")
    val movieVideoDetails: List<MovieVideoDetail>
)