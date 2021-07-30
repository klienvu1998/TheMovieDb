package com.hyvu.themoviedb.utils

object Constraints {
    const val YOUTUBE_API = "AIzaSyB3X4nGBuX2589aNRMKuKXhzOBqAjGbcXY"
    const val YOUTUBE_THUMBNAIL = "https://img.youtube.com/vi/"
    fun getYoutubeThumbnailLink(videoId: String) = "$YOUTUBE_THUMBNAIL$videoId/hqdefault.jpg"
}