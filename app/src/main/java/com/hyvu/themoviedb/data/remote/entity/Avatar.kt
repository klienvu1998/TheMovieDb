package com.hyvu.themoviedb.data.remote.entity


import com.google.gson.annotations.SerializedName

data class Avatar(
    @SerializedName("gravatar")
    val gravatar: GravatarX?,
    @SerializedName("tmdb")
    val tmdb: Tmdb?
)