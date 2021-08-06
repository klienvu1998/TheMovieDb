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
import com.hyvu.themoviedb.databinding.ItemPosterBinding
import com.hyvu.themoviedb.utils.Utils
import com.hyvu.themoviedb.view.MainActivity

class HomeCategoryMovieChildAdapter(
        private val context: Context?,
        private val listMovieDetails: List<MovieDetail>?,
        private val listener: Listener,
): RecyclerView.Adapter<HomeCategoryMovieChildAdapter.ViewHolder>() {

    interface Listener {

    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val mBinding = ItemPosterBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.item_poster, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movieDetail = listMovieDetails?.get(position)
        holder.mBinding.apply {
            Utils.loadGlideImage(context, BASE_IMG_LOW_QUALITY_URL, movieDetail?.posterPath, imgPoster, R.drawable.ic_image_not_supported)
            tvMovieName.text = movieDetail?.title
            tvRating.text = movieDetail?.voteAverage.toString()
            movieContainer.setOnClickListener {
                (context as MainActivity).showMovieDetails(movieDetail!!)
            }
        }
    }

    override fun getItemCount(): Int {
        return listMovieDetails?.size ?: 0
    }
}