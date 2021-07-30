package com.hyvu.themoviedb.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.data.api.BASE_IMG_URL
import com.hyvu.themoviedb.data.entity.MoviesByGenre

class HomeCategoryMovieChildAdapter(
        private val context: Context?,
        private val moviesByGenre: MoviesByGenre?,
        private val listener: Listener,
): RecyclerView.Adapter<HomeCategoryMovieChildAdapter.ViewHolder>() {

    interface Listener {
        fun onClickedMovie(movieId: Int)
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val tvName = view.findViewById<TextView>(R.id.tv_movie_name)
        val tvRating = view.findViewById<TextView>(R.id.tv_rating)
        val imgBanner = view.findViewById<ImageView>(R.id.img_banner)
        val movieContainer = view.findViewById<ConstraintLayout>(R.id.movie_container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.item_banner, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = moviesByGenre?.movieOverviews?.get(position)
        if (context != null) {
            val option = RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ic_image)
                .error(R.drawable.ic_image_not_supported)
            Glide.with(context)
                .load(BASE_IMG_URL + movie?.posterPath)
                .apply(option)
                .into(holder.imgBanner)
        }
        holder.tvName.text = movie?.title
        holder.tvRating.text = movie?.voteAverage.toString()
        holder.movieContainer.setOnClickListener {
            listener.onClickedMovie(movie?.id!!)
        }
    }

    override fun getItemCount(): Int {
        return moviesByGenre?.movieOverviews?.size ?: 0
    }
}