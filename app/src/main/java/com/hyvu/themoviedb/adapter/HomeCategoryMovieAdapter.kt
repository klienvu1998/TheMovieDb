package com.hyvu.themoviedb.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.data.entity.Genre
import com.hyvu.themoviedb.data.entity.MoviesByGenre
import java.util.concurrent.ConcurrentHashMap

class HomeCategoryMovieAdapter(
    private val context: Context?,
    private val listener: Listener,
): RecyclerView.Adapter<HomeCategoryMovieAdapter.ViewHolder>() {

    interface Listener {
        fun onClickedSeeAll()
        fun onClickedMovie(movieId: Int)
    }

    private var listMovieCategory: Map<Genre, MoviesByGenre>? = ConcurrentHashMap<Genre, MoviesByGenre>()

    fun setAdapterData(data: Map<Genre, MoviesByGenre>) {
        this.listMovieCategory = data
        notifyDataSetChanged()
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tv_category_name)
        val rcvMovie: RecyclerView = view.findViewById(R.id.rcv_movie)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val genre = listMovieCategory?.keys?.toList()?.get(position)
        holder.tvName.text = genre?.name
        holder.rcvMovie.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val adapter = HomeCategoryMovieChildAdapter(context, listMovieCategory?.get(genre), childListener)
        holder.rcvMovie.adapter = adapter
    }

    override fun getItemCount(): Int {
        return this.listMovieCategory?.keys?.size ?: 0
    }

    private val childListener = object : HomeCategoryMovieChildAdapter.Listener {
        override fun onClickedMovie(movieId: Int) {
            listener.onClickedMovie(movieId)
        }

    }
}