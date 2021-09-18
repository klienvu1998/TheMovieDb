package com.hyvu.themoviedb.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.data.entity.Genre
import com.hyvu.themoviedb.data.entity.MovieDetail
import com.hyvu.themoviedb.databinding.ItemCategoryBinding

class UserCategoryAdapter(
    private val context: Context?,
    private val listener: Listener,
    private val listMovie: Map<Genre, List<MovieDetail>>
): RecyclerView.Adapter<UserCategoryAdapter.ViewHolder>() {

    interface Listener {
        fun onClickedSeeAll(genre: Genre)
    }

    fun setData(listMovie: Map<Genre, List<MovieDetail>>) {
        (this.listMovie as LinkedHashMap).putAll(listMovie)
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val mBinding = ItemCategoryBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = listMovie.keys.toList()[position]
        holder.mBinding.apply {
            tvCategoryName.text = movie.name
            containerSeeAll.setOnClickListener {
                listener.onClickedSeeAll(movie)
            }
            val adapter = HomeCategoryMovieChildAdapter(context, listMovie[movie], childListener)
            rcvMovie.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            rcvMovie.adapter = adapter
        }
    }

    override fun getItemCount(): Int {
        return listMovie.size
    }

    private val childListener = object : HomeCategoryMovieChildAdapter.Listener {

    }
}