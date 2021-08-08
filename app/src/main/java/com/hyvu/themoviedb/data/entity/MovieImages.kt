package com.hyvu.themoviedb.data.entity


import com.google.gson.annotations.SerializedName

data class MovieImages(
    @SerializedName("backdrops")
    val backdrops: List<Backdrop>,
    @SerializedName("id")
    val id: Int,
    @SerializedName("logos")
    val logos: List<Logo>,
    @SerializedName("posters")
    val posters: List<Poster>
)