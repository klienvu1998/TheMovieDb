package com.hyvu.themoviedb.utils

object Constraints {
    const val YOUTUBE_API = "AIzaSyB3X4nGBuX2589aNRMKuKXhzOBqAjGbcXY"

    const val YOUTUBE_THUMBNAIL = "https://img.youtube.com/vi/"
    fun getYoutubeThumbnailLink(videoId: String) = "$YOUTUBE_THUMBNAIL$videoId/hqdefault.jpg"

    const val NETWORK_PAGE_SIZE = 50
    const val MAX_ITEM_PER_SCROLL = 200


    const val TRENDING_MOVIE = "Trending"
}