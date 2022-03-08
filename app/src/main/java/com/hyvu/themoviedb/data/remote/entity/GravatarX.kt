package com.hyvu.themoviedb.data.remote.entity


import com.google.gson.annotations.SerializedName

data class GravatarX(
    @SerializedName("hash")
    val hash: String?
)