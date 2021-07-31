package com.hyvu.themoviedb.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.data.api.BASE_IMG_HIGH_QUALITY_URL
import com.hyvu.themoviedb.data.api.BASE_IMG_LOW_QUALITY_URL
import com.hyvu.themoviedb.data.api.BASE_IMG_MEDIUM_QUALITY_URL
import com.hyvu.themoviedb.data.entity.MovieDetail
import com.hyvu.themoviedb.data.entity.TikMovie
import com.hyvu.themoviedb.data.repository.MovieRepository
import com.hyvu.themoviedb.databinding.ItemTikmovieBinding
import com.hyvu.themoviedb.utils.Utils
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import java.util.*

class TikMoviePagingViewPagerAdapter(
        private val context: Context?,
        private val listener: Listener,
): PagingDataAdapter<MovieDetail, TikMoviePagingViewPagerAdapter.ViewHolder>(REPO_COMPARATOR) {

    companion object {
        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<MovieDetail>() {
            override fun areItemsTheSame(oldItem: MovieDetail, newItem: MovieDetail): Boolean =
                    oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: MovieDetail, newItem: MovieDetail): Boolean =
                    oldItem == newItem
        }
    }

    interface Listener {
        fun showMovieDetails(movieId: Int)
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val mBinding = ItemTikmovieBinding.bind(view)
        val youTubePlayer: YouTubePlayer? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.item_tikmovie, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movieDetail = getItem(position) as MovieDetail
        Utils.loadGlideImage(context, BASE_IMG_HIGH_QUALITY_URL, movieDetail.getBackdropImage(), holder.mBinding.imgBackdrop)
        holder.mBinding.detailContainer.apply {
            tvTitle.text = movieDetail.title
            tvTitle.isSelected = true
            ratingBar.max = 10
            ratingBar.rating = ((movieDetail.voteAverage * 5) / 10).toFloat()
            tvVoteCount.text = movieDetail.voteCount.toString()
            tvReleaseDate.text = movieDetail.releaseDate
            rcvGenres.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = GenresAdapter(context, MovieRepository.responseListMovieGenre.value?.genres!!.filter { movieDetail.genreIds.contains(it.id) })
            }
            Utils.loadGlideImage(context, BASE_IMG_LOW_QUALITY_URL, movieDetail.posterPath, imgPoster)
        }
        holder.mBinding.detailParentContainer.setOnClickListener {
            listener.showMovieDetails(movieDetail.id)
        }
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        holder.youTubePlayer?.pause()
        super.onViewDetachedFromWindow(holder)
    }

}