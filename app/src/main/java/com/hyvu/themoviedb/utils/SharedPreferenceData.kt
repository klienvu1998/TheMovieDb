package com.hyvu.themoviedb.utils

import android.content.Context
import javax.inject.Inject

class SharedPreferenceData @Inject constructor(private val context: Context): Storage {

    companion object {
        const val SESSION_ID = "SESSION_ID"
        const val IS_NIGHT_MODE = "IS_NIGHT_THEME"
    }

    private val sharedPreferences = context.getSharedPreferences("TheMovieDb", Context.MODE_PRIVATE)

    override fun setString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    override fun getString(key: String): String {
        return sharedPreferences.getString(key, "") ?: ""
    }

    override fun setBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    override fun getBoolean(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }


}