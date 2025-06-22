package com.hyvu.themoviedb.data.remote.entity

import com.google.gson.annotations.SerializedName

data class GuestSessionResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("expires_at")
    val expiredAt: String,
    @SerializedName("guest_session_id")
    val guestSessionId: String
) {


}