package com.hyvu.themoviedb.data.remote.entity

import com.google.gson.annotations.SerializedName

data class Genres (
    @SerializedName("genres")
    val genres: List<Genre>,
)