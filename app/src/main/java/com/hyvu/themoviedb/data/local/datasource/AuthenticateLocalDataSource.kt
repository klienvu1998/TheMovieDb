package com.hyvu.themoviedb.data.local.datasource

import com.hyvu.themoviedb.data.local.entity.GuestSession
import com.hyvu.themoviedb.data.local.entity.UserSession

interface AuthenticateLocalDataSource {

    fun saveGuestSession(guestSessionEntity: GuestSession)

    fun getGuestSession(): GuestSession?

    fun clearGuestSession()

    fun saveUserSession(userSession: UserSession)

    fun getUserSession(): UserSession?

    fun clearUserSession()

}