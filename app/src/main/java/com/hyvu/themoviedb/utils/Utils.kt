package com.hyvu.themoviedb.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.hyvu.themoviedb.R

class Utils {
    companion object {
        fun loadGlideImage(context: Context?, baseUrl: String, url: String?, imageView: ImageView, errorDrawable: Int) {
            if (context != null && !url.isNullOrEmpty()) {
                Glide.with(context)
                    .load(baseUrl + url)
                    .centerCrop()
                    .error(errorDrawable)
                    .into(object : CustomViewTarget<ImageView, Drawable>(imageView) {
                        override fun onLoadFailed(errorDrawable: Drawable?) {
                            imageView.setImageDrawable(errorDrawable)
                            imageView.setColorFilter(R.attr.colorSecondary)
                        }

                        override fun onResourceReady(
                            resource: Drawable,
                            transition: Transition<in Drawable>?
                        ) {
                            imageView.setColorFilter(android.R.color.transparent)
                            imageView.setImageDrawable(resource)
                        }

                        override fun onResourceCleared(placeholder: Drawable?) {
                        }

                    })
            }
        }

        fun loadGlidePosterImage(context: Context?, baseUrl: String, url: String?, imageView: ImageView, errorDrawable: Int) {
            if (context != null && !url.isNullOrEmpty()) {
                Glide.with(context)
                    .load(baseUrl + url)
                    .centerCrop()
                    .override(100, 150)
                    .error(errorDrawable)
                    .into(object : CustomViewTarget<ImageView, Drawable>(imageView) {
                        override fun onLoadFailed(errorDrawable: Drawable?) {
                            imageView.setImageDrawable(errorDrawable)
                            imageView.setColorFilter(R.attr.colorSecondary)
                        }

                        override fun onResourceReady(
                            resource: Drawable,
                            transition: Transition<in Drawable>?
                        ) {
                            imageView.setColorFilter(android.R.color.transparent)
                            imageView.setImageDrawable(resource)
                        }

                        override fun onResourceCleared(placeholder: Drawable?) {
                        }

                    })
            }
        }
    }
}