package com.hyvu.themoviedb.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.data.api.BASE_IMG_LOW_QUALITY_URL
import com.hyvu.themoviedb.data.entity.Cast
import com.hyvu.themoviedb.data.entity.Credits
import com.hyvu.themoviedb.databinding.ItemCreditBinding
import com.hyvu.themoviedb.utils.Utils

class MovieCreditAdapter(
    private val context: Context?,
    private var credit: Credits
): RecyclerView.Adapter<MovieCreditAdapter.ViewHolder>() {
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val mBinding = ItemCreditBinding.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.item_credit, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cast = credit.cast?.get(position)
        Utils.loadGlideImage(context, BASE_IMG_LOW_QUALITY_URL, cast?.profilePath, holder.mBinding.imgCast, R.drawable.ic_user)
        holder.mBinding.tvCast.text = cast?.name
        holder.mBinding.tvCharacter.text = cast?.character
    }

    override fun getItemCount(): Int {
        return credit.cast?.size ?: 0
    }

}