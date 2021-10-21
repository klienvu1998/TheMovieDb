package com.hyvu.themoviedb.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.data.api.BASE_IMG_HIGH_QUALITY_URL
import com.hyvu.themoviedb.data.api.BASE_IMG_LOW_QUALITY_URL
import com.hyvu.themoviedb.data.entity.Genre
import com.hyvu.themoviedb.data.entity.Genres
import com.hyvu.themoviedb.data.entity.MovieDetail
import com.hyvu.themoviedb.databinding.ItemTikmovieBinding
import com.hyvu.themoviedb.utils.Utils
import com.hyvu.themoviedb.view.activity.MainActivity
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer

class TikMoviePagingDataAdapter(
        private val context: Context?,
        private val listener: Listener,
        private val listMovieGenre: List<Genre>
): PagingDataAdapter<MovieDetail, TikMoviePagingDataAdapter.ViewHolder>(REPO_COMPARATOR) {

    companion object {
        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<MovieDetail>() {
            override fun areItemsTheSame(oldItem: MovieDetail, newItem: MovieDetail): Boolean =
                    oldItem.movieId == newItem.movieId

            override fun areContentsTheSame(oldItem: MovieDetail, newItem: MovieDetail): Boolean =
                    oldItem == newItem
        }
    }

    interface Listener {

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
        Utils.loadGlideImage(context, BASE_IMG_HIGH_QUALITY_URL, movieDetail.getBackdropImage(), holder.mBinding.imgBackdrop, R.drawable.ic_image_not_supported)
        holder.mBinding.detailInfoContainer.apply {
            tvTitle.text = movieDetail.title
            tvTitle.isSelected = true
            ratingBar.max = 10
            ratingBar.rating = ((movieDetail.voteAverage * 5) / 10).toFloat()
            tvVoteCount.text = movieDetail.voteCount.toString()
            tvReleaseDate.text = movieDetail.releaseDate
            rcvGenres.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = GenresAdapter(context, listMovieGenre.filter { movieDetail.genreIds.contains(it.id) })
            }
            Utils.loadGlideImage(context, BASE_IMG_LOW_QUALITY_URL, movieDetail.posterPath, imgPoster, R.drawable.ic_image_not_supported)
        }
        holder.mBinding.detailParentContainer.setOnClickListener {
            (context as MainActivity).showMovieDetails(movieDetail)
        }
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        holder.youTubePlayer?.pause()
        super.onViewDetachedFromWindow(holder)
    }

}