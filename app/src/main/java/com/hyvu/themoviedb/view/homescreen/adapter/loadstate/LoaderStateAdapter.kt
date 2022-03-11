package com.hyvu.themoviedb.view.homescreen.adapter.loadstate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hyvu.themoviedb.R

class LoaderStateAdapter(private val retry: () -> Unit): LoadStateAdapter<LoaderStateAdapter.LoaderViewHolder>() {

    class LoaderViewHolder(view: View, retry: () -> Unit): RecyclerView.ViewHolder(view) {
        companion object {
            fun getInstance(parent: ViewGroup, retry: () -> Unit): LoaderViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val view = inflater.inflate(R.layout.item_paging_loader, parent, false)
                return LoaderViewHolder(view, retry)
            }
        }

        fun bind(loadState: LoadState) {

        }
    }

    override fun onBindViewHolder(holder: LoaderViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): LoaderViewHolder {
        return LoaderViewHolder.getInstance(parent,retry)
    }

}