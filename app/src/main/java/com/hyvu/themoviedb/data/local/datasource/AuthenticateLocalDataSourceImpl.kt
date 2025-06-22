package com.hyvu.themoviedb.data.local.datasource

import android.util.Log
import com.google.gson.Gson
import com.hyvu.themoviedb.data.local.entity.GuestSession
import com.hyvu.themoviedb.data.local.entity.UserSession
import com.hyvu.themoviedb.utils.Storage
import javax.inject.Inject

class AuthenticateLocalDataSourceImpl @Inject constructor(
    private val storage: Storage,
    private val gson: Gson
): AuthenticateLocalDataSource {

    companion object {
        private const val TAG = "AuthenticateLocalDataSourceImpl"

        private const val KEY_GUEST_SESSION_JSON = "guest_session_json"
        private const val KEY_USER_SESSION_JSON = "guest_session_json"
    }

    override fun saveGuestSession(guestSessionEntity: GuestSession) {
        try {
            val sessionJson = gson.toJson(guestSessionEntity)
            storage.setString(KEY_GUEST_SESSION_JSON, sessionJson)
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        }
    }

    override fun getGuestSession(): GuestSession? {
        return try {
            val session = gson.fromJson(storage.getString(KEY_GUEST_SESSION_JSON), GuestSession::class.java)
            return if (session.guestSessionId.isNotEmpty()) {
                session
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            null
        }
    }

    override fun clearGuestSession() {
        storage.setString(KEY_GUEST_SESSION_JSON, "")
    }

    override fun saveUserSession(userSession: UserSession) {
        try {
            val sessionJson = gson.toJson(userSession)
            storage.setString(KEY_USER_SESSION_JSON, sessionJson)
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        }
    }

    override fun getUserSession(): UserSession? {
        return try {
            val session = gson.fromJson(storage.getString(KEY_USER_SESSION_JSON), UserSession::class.java)
            return if (session.sessionId.isNotEmpty()) {
                session
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
            null
        }
    }

    override fun clearUserSession() {
        storage.setString(KEY_USER_SESSION_JSON, "")
    }

}