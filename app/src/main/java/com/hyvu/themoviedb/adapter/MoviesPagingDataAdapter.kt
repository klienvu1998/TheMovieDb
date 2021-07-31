package com.hyvu.themoviedb.adapter

import android.content.Context
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.data.api.BASE_IMG_LOW_QUALITY_URL
import com.hyvu.themoviedb.data.entity.MovieDetail
import com.hyvu.themoviedb.databinding.ItemPosterBinding
import com.hyvu.themoviedb.utils.Utils
import com.hyvu.themoviedb.view.MainActivity

class MoviesPagingDataAdapter(
    private val context: Context?,
    private val listener: Listener
): PagingDataAdapter<MovieDetail, MoviesPagingDataAdapter.ViewHolder>(
    REPO_COMPARATOR) {

    interface Listener {
        fun showMovieDetails(movieId: Int)
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val mBinding = ItemPosterBinding.bind(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = getItem(position) as MovieDetail
        holder.mBinding.apply {
            tvMovieName.text = movie.originalTitle
            tvRating.text = Utils.convertVoteToRating(movie.voteAverage).toString()
            Utils.loadGlideImage(context, BASE_IMG_LOW_QUALITY_URL, movie.posterPath, imgPoster)
            movieContainer.setOnClickListener {
                listener.showMovieDetails(movie.id)
            }
        }
    }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.item_poster_grid, parent, false)
        return ViewHolder(v)
    }

    companion object {
        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<MovieDetail>() {
            override fun areItemsTheSame(oldItem: MovieDetail, newItem: MovieDetail): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: MovieDetail, newItem: MovieDetail): Boolean =
                oldItem == newItem
        }
    }
}