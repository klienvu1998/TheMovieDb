package com.hyvu.themoviedb.data.remote.entity

import com.google.gson.annotations.SerializedName

data class UserSessionResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("session_id")
    val sessionId: String
)
