package com.hyvu.themoviedb.data.local.entity

import android.util.Log
import com.hyvu.themoviedb.data.remote.entity.RequestTokenResponse
import com.hyvu.themoviedb.utils.TimeUtils

data class RequestToken(
    val expireAt: Long,
    val requestToken: String
) {

    companion object {
        private const val TAG = "RequestToken"

        fun mapData(requestTokenResponse: RequestTokenResponse?): RequestToken? {
            requestTokenResponse ?: return null

            return try {
                RequestToken(
                    requestToken = requestTokenResponse.requestToken,
                    expireAt = TimeUtils.parseDateTimeToUnixTimestampLegacy(requestTokenResponse.expiresAt) ?: Long.MAX_VALUE
                )
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
                null
            }
        }
    }

}