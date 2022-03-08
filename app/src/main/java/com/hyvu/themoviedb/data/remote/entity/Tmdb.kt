package com.hyvu.themoviedb.data.remote.entity


import com.google.gson.annotations.SerializedName

data class Tmdb(
    @SerializedName("avatar_path")
    val avatarPath: Any?
)