package com.hyvu.themoviedb.data.entity


import com.google.gson.annotations.SerializedName

data class AuthenticateToken(
    @SerializedName("expires_at")
    val expiresAt: String,
    @SerializedName("request_token")
    val requestToken: String,
    @SerializedName("success")
    val success: Boolean
)