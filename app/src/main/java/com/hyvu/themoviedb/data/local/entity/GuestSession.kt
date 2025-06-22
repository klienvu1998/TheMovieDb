package com.hyvu.themoviedb.data.local.entity

import android.util.Log
import com.hyvu.themoviedb.data.remote.entity.GuestSessionResponse
import com.hyvu.themoviedb.utils.TimeUtils

data class GuestSession(
    val guestSessionId: String,
    val expiredAt: Long
) {

    companion object {
        private const val TAG = "GuestSessionEntity"

        fun mapData(guestSessionResponse: GuestSessionResponse?): GuestSession? {
            guestSessionResponse ?: return null

            return try {
                return GuestSession(
                    guestSessionId = guestSessionResponse.guestSessionId,
                    expiredAt = TimeUtils.parseDateTimeToUnixTimestampLegacy(guestSessionResponse.expiredAt) ?: Long.MAX_VALUE
                )
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
                null
            }
        }
    }

}