package com.hyvu.themoviedb.view.homescreen.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.data.remote.entity.Genre
import java.lang.ref.WeakReference

class GenresAdapter(
    private val weakContext: WeakReference<Context>,
    private val listGenres: List<Genre>
): RecyclerView.Adapter<GenresAdapter.ViewHolder>() {

    private val context = weakContext.get()

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val tvGenre: TextView = view.findViewById(R.id.tv_genre)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.item_genre, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val genre = listGenres[position]
        holder.tvGenre.text = genre.name
    }

    override fun getItemCount(): Int {
        return listGenres.size - 1
    }
}