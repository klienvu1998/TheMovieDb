package com.hyvu.themoviedb.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.data.api.BASE_IMG_MEDIUM_QUALITY_URL
import com.hyvu.themoviedb.data.entity.MovieDetail
import com.hyvu.themoviedb.data.entity.TrendingMovie
import com.hyvu.themoviedb.utils.Utils

class ViewPagerSliderAdapter(
        private val context: Context?,
        private val listSliderViewPagerItem: List<MovieDetail>?,
        private val listener: Listener,
): RecyclerView.Adapter<ViewPagerSliderAdapter.ViewHolder>() {

    interface Listener {
        fun onTrendingMovieClicked(movieId: Int)
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val imageView = view.findViewById<ImageView>(R.id.img_slide)
        val tvMovieName = view.findViewById<TextView>(R.id.tv_movie_name)
        fun setImage(trendingMovie: MovieDetail) {
            Utils.loadGlideImage(context, BASE_IMG_MEDIUM_QUALITY_URL, trendingMovie.backdropPath, imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.item_slide, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = listSliderViewPagerItem?.get(position)
        movie?.let { holder.setImage(it) }
        holder.tvMovieName.text = movie?.originalTitle
        holder.imageView.setOnClickListener {
            listener.onTrendingMovieClicked(movie!!.id)
        }
    }

    override fun getItemCount(): Int {
        return listSliderViewPagerItem?.size ?: 0
    }
}