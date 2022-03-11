package com.hyvu.themoviedb.view.homescreen.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.data.remote.api.BASE_IMG_MEDIUM_QUALITY_URL
import com.hyvu.themoviedb.data.remote.entity.MovieDetail
import com.hyvu.themoviedb.utils.Utils
import com.hyvu.themoviedb.view.homescreen.MainActivity
import java.lang.ref.WeakReference

class ViewPagerSliderAdapter(
        private val weakContext: WeakReference<Context>,
        private val listSliderViewPagerItem: List<MovieDetail>?,
        private val listener: Listener,
): RecyclerView.Adapter<ViewPagerSliderAdapter.ViewHolder>() {

    private val context = weakContext.get()

    interface Listener {

    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val imageView = view.findViewById<ImageView>(R.id.img_slide)
        val tvMovieName = view.findViewById<TextView>(R.id.tv_movie_name)
        fun setImage(trendingMovie: MovieDetail) {
            Utils.loadGlideImage(context, BASE_IMG_MEDIUM_QUALITY_URL, trendingMovie.backdropPath, imageView, R.drawable.ic_image_not_supported)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.item_slide, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movieDetail = listSliderViewPagerItem?.get(position)
        movieDetail?.let { holder.setImage(it) }
        holder.tvMovieName.text = movieDetail?.originalTitle
        holder.imageView.setOnClickListener {
            if (movieDetail != null) {
                (context as MainActivity).showMovieDetails(movieDetail)
            }
        }
    }

    override fun getItemCount(): Int {
        return listSliderViewPagerItem?.size ?: 0
    }
}