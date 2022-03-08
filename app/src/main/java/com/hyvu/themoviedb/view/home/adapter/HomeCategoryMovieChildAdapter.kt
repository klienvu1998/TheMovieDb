package com.hyvu.themoviedb.view.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.data.remote.api.BASE_IMG_LOW_QUALITY_URL
import com.hyvu.themoviedb.data.remote.entity.MovieDetail
import com.hyvu.themoviedb.databinding.ItemPosterBinding
import com.hyvu.themoviedb.utils.Utils
import com.hyvu.themoviedb.view.home.MainActivity

class HomeCategoryMovieChildAdapter(
        private val context: Context?,
        private val listMovieDetails: List<MovieDetail>?,
): RecyclerView.Adapter<HomeCategoryMovieChildAdapter.ViewHolder>() {

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
            tvMovieName.text = movieDetail?.title
            tvRating.text = movieDetail?.voteAverage.toString()
            movieContainer.setOnClickListener {
                (context as MainActivity).showMovieDetails(movieDetail!!)
            }
            Utils.loadGlidePosterImage(context, BASE_IMG_LOW_QUALITY_URL, movieDetail?.posterPath, imgPoster, R.drawable.ic_image_not_supported)
        }
    }

    override fun getItemCount(): Int {
        return listMovieDetails?.size ?: 0
    }
}