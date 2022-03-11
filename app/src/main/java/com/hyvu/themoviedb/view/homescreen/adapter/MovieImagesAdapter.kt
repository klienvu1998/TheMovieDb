package com.hyvu.themoviedb.view.homescreen.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.data.remote.api.BASE_IMG_MEDIUM_QUALITY_URL
import com.hyvu.themoviedb.data.remote.entity.Backdrop
import com.hyvu.themoviedb.databinding.ItemImageBinding
import com.hyvu.themoviedb.utils.Utils
import java.lang.ref.WeakReference

class MovieImagesAdapter(
    private val weakContext: WeakReference<Context>,
    private val listener: Listener,
): RecyclerView.Adapter<MovieImagesAdapter.ViewHolder>() {

    private val context = weakContext.get()

    interface Listener {
        fun onImageClicked(backdrops: List<Backdrop>, position: Int)
    }

    private var listImages: List<Backdrop> = ArrayList()

    fun updateData(data: List<Backdrop>) {
        this.listImages = data
        notifyDataSetChanged()
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val mBinding = ItemImageBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        val backdrop = listImages[position]
        holder.mBinding.apply {
            Utils.loadGlideImage(context, BASE_IMG_MEDIUM_QUALITY_URL, backdrop.filePath, imgBackdrop, R.drawable.ic_image_not_supported)
            imgBackdrop.setOnClickListener {
                listener.onImageClicked(listImages, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return listImages.size
    }
}