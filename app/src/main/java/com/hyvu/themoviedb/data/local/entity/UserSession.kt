package com.hyvu.themoviedb.data.local.entity

import android.util.Log
import com.hyvu.themoviedb.data.remote.entity.UserSessionResponse

data class UserSession(
    val sessionId: String
) {

    companion object {
        private const val TAG = "UserSessionEntity"

        fun mapData(userSessionResponse: UserSessionResponse?): UserSession? {
            userSessionResponse ?: return null

            return try {
                UserSession(
                    sessionId = userSessionResponse.sessionId
                )
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
                null
            }
        }
    }

}