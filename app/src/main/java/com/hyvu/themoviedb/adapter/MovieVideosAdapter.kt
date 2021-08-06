package com.hyvu.themoviedb.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.data.entity.MovieVideoDetail
import com.hyvu.themoviedb.utils.Constraints
import com.hyvu.themoviedb.utils.Utils

class MovieVideosAdapter(
        private val context: Context?,
        private val listVideos: List<MovieVideoDetail>,
        private val listener: Listener
): RecyclerView.Adapter<MovieVideosAdapter.ViewBinder>() {

    interface Listener {
        fun onItemClicked(videoDetail: MovieVideoDetail)
    }

    class ViewBinder(view: View): RecyclerView.ViewHolder(view) {
        val btnPlay = view.findViewById<TextView>(R.id.btn_play)
        val imgVideos = view.findViewById<ImageView>(R.id.img_slide)
        val tvName = view.findViewById<TextView>(R.id.tv_movie_name)
        val container = view.findViewById<CardView>(R.id.card_container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewBinder {
        val v = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false)
        return ViewBinder(v)
    }

    override fun onBindViewHolder(holder: ViewBinder, position: Int) {
        val videoDetail = listVideos[position]
        Utils.loadGlideImage(context, "", Constraints.getYoutubeThumbnailLink(videoDetail.key), holder.imgVideos, R.drawable.ic_image_not_supported)
        holder.tvName.text = videoDetail.name
        holder.container.setOnClickListener {
            listener.onItemClicked(videoDetail)
        }
    }

    override fun getItemCount(): Int {
        return listVideos.size
    }
}