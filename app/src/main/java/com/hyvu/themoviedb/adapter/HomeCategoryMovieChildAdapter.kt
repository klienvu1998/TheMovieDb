package com.hyvu.themoviedb.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.data.api.BASE_IMG_LOW_QUALITY_URL
import com.hyvu.themoviedb.data.entity.MovieDetail
import com.hyvu.themoviedb.utils.Utils

class HomeCategoryMovieChildAdapter(
        private val context: Context?,
        private val listMovieDetails: List<MovieDetail>?,
        private val listener: Listener,
): RecyclerView.Adapter<HomeCategoryMovieChildAdapter.ViewHolder>() {

    interface Listener {
        fun onClickedMovie(movieId: Int)
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val tvName = view.findViewById<TextView>(R.id.tv_movie_name)
        val tvRating = view.findViewById<TextView>(R.id.tv_rating)
        val imgBanner = view.findViewById<ImageView>(R.id.img_poster)
        val movieContainer = view.findViewById<ConstraintLayout>(R.id.movie_container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.item_poster, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = listMovieDetails?.get(position)
        Utils.loadGlideImage(context, BASE_IMG_LOW_QUALITY_URL, movie?.posterPath, holder.imgBanner)
        holder.tvName.text = movie?.title
        holder.tvRating.text = movie?.voteAverage.toString()
        holder.movieContainer.setOnClickListener {
            listener.onClickedMovie(movie?.id ?: 0)
        }
    }

    override fun getItemCount(): Int {
        return listMovieDetails?.size ?: 0
    }
}