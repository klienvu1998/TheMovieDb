package com.hyvu.themoviedb.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.hyvu.themoviedb.R

class Utils {
    companion object {
        fun convertVoteToRating(voteAverage: Double): Float {
            return ((voteAverage * 5) / 10).toFloat()
        }

        fun loadGlideImage(context: Context?, baseUrl: String, url: String?, imageView: ImageView) {
            if (context != null && !url.isNullOrEmpty()) {
                Glide.with(context)
                        .load(baseUrl + url)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .error(R.drawable.ic_image_not_supported)
                        .into(imageView)
            }
        }
    }
}