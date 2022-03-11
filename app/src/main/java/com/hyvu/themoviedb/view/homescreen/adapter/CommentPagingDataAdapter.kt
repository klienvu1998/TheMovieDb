package com.hyvu.themoviedb.view.homescreen.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.data.remote.api.BASE_IMG_LOW_QUALITY_URL
import com.hyvu.themoviedb.data.remote.entity.Comment
import com.hyvu.themoviedb.databinding.ItemCommentBinding
import com.hyvu.themoviedb.utils.Utils
import java.lang.ref.WeakReference

class CommentPagingDataAdapter(
    private val weakContext: WeakReference<Context>,
): PagingDataAdapter<Comment, CommentPagingDataAdapter.ViewHolder>(
    REPO_COMPARATOR) {

    val context = weakContext.get()

    companion object {
        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<Comment>() {
            override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean =
                oldItem == newItem
        }
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val mBinding = ItemCommentBinding.bind(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = getItem(position) as Comment
        holder.mBinding.apply {
            Utils.loadGlideImage(context, BASE_IMG_LOW_QUALITY_URL, comment.authorDetails.avatarPath, imgUser, R.drawable.ic_user)
            tvUserName.text = comment.author
            tvUserComment.text = comment.content
            tvUserCreatedTime.text = comment.createdAt
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false)
        return ViewHolder(v)
    }
}