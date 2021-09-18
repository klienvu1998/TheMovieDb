package com.hyvu.themoviedb.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.Target
import com.hyvu.themoviedb.R

class Utils {
    companion object {
        fun convertVoteToRating(voteAverage: Double): Float {
            return ((voteAverage * 5) / 10).toFloat()
        }

        fun loadGlideImage(context: Context?, baseUrl: String, url: String?, imageView: ImageView, errorDrawable: Int) {
            if (context != null && !url.isNullOrEmpty()) {
                Glide.with(context)
                    .load(baseUrl + url)
                    .centerCrop()
                    .error(errorDrawable)
                    .into(imageView)
            }
        }
    }
}