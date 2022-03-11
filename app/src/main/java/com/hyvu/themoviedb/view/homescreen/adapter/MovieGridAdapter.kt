package com.hyvu.themoviedb.view.homescreen.adapter

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
import com.hyvu.themoviedb.view.homescreen.MainActivity
import java.lang.ref.WeakReference

class MovieGridAdapter(
    private val weakContext: WeakReference<Context>,
    private val listMovie: List<MovieDetail>
): RecyclerView.Adapter<MovieGridAdapter.ViewHolder>() {

    private val context= weakContext.get()

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val mBinding = ItemPosterBinding.bind(view)
    }

    fun setData(listMovie: List<MovieDetail>) {
        (this.listMovie as ArrayList).addAll(listMovie)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.item_poster_grid, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movieDetail = listMovie[position]
        holder.mBinding.apply {
            tvMovieName.text = movieDetail.originalTitle
            tvRating.text = movieDetail.voteAverage.toString()
            Utils.loadGlideImage(context, BASE_IMG_LOW_QUALITY_URL, movieDetail.posterPath, imgPoster, R.drawable.ic_image_not_supported)
            movieContainer.setOnClickListener {
                (context as MainActivity).showMovieDetails(movieDetail)
            }
        }
    }

    override fun getItemCount(): Int {
        return listMovie.size
    }
}