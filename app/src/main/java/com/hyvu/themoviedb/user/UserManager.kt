package com.hyvu.themoviedb.user

import com.hyvu.themoviedb.storage.SharedPreferenceData
import com.hyvu.themoviedb.storage.Storage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserManager @Inject constructor(private val storage: Storage) {

    var accountId: Int? = null
    val sessionId: String
        get() = storage.getString(SharedPreferenceData.SESSION_ID)
    val isNightMode: Boolean
        get() = storage.getBoolean(SharedPreferenceData.IS_NIGHT_MODE)

    fun saveSessionId(sessionId: String) {
        storage.setString(SharedPreferenceData.SESSION_ID, sessionId)
    }

    fun saveIsNightMode(isNightMode: Boolean) {
        storage.setBoolean(SharedPreferenceData.IS_NIGHT_MODE, isNightMode)
    }

}