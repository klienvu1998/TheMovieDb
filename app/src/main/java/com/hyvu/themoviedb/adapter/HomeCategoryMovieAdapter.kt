package com.hyvu.themoviedb.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.data.entity.*
import com.hyvu.themoviedb.databinding.ItemBannerBinding
import com.hyvu.themoviedb.databinding.ItemCategoryBinding
import java.util.*
import kotlin.collections.LinkedHashMap

class HomeCategoryMovieAdapter(
    private val context: Context?,
    private val listener: Listener,
    private var mapMovieCategories: LinkedHashMap<Genre, List<MovieDetail>>,
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val viewPool = RecyclerView.RecycledViewPool()
    companion object {
        const val TRENDING_CATEGORY = 0
        const val MOVIE_BY_GENRE = 1
    }

    interface Listener {
        fun onClickedSeeAll(genre: Genre)
        fun onClickedTrending()
    }

    fun addMovieData(data: Pair<Genre, List<MovieDetail>>) {
        this.mapMovieCategories[data.first] = data.second
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) TRENDING_CATEGORY else MOVIE_BY_GENRE
    }

    inner class MovieByGenreViewHolder(parent: ViewGroup): RecyclerView.ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_category, parent, false)) {
        val mBinding = ItemCategoryBinding.bind(itemView)
    }

    inner class TrendingMovieViewHolder(parent: ViewGroup): RecyclerView.ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_banner, parent, false)) {
        val mBinding: ItemBannerBinding = ItemBannerBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            TRENDING_CATEGORY -> TrendingMovieViewHolder(parent)
            MOVIE_BY_GENRE -> MovieByGenreViewHolder(parent)
            else -> MovieByGenreViewHolder(parent)
        }
    }

    override fun getItemCount(): Int {
        return this.mapMovieCategories.keys.size
    }

    private val childListener = object : HomeCategoryMovieChildAdapter.Listener {

    }

    override fun onBindViewHolder(holderMovieByGenre: RecyclerView.ViewHolder, position: Int) {
        if (position == TRENDING_CATEGORY) {
            val genre = mapMovieCategories.keys.toList()[0]
            (holderMovieByGenre as TrendingMovieViewHolder).mBinding.apply {
                containerSeeAll.setOnClickListener {
                    listener.onClickedTrending()
                }
                val adapterViewPagerSlider = ViewPagerSliderAdapter(context, mapMovieCategories[genre]?.filterTo(
                        LinkedList<MovieDetail>(), { movie -> movie.backdropPath != null && movie.backdropPath.isNotEmpty() }), listenerViewPagerSliderAdapter
                )
                viewPagerTrending.apply {
                    adapter = adapterViewPagerSlider
                    clipToPadding = false
                    clipChildren = false
                    offscreenPageLimit = 3
                    getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
                }
                val compositePageTransformer = CompositePageTransformer()
                compositePageTransformer.addTransformer(MarginPageTransformer(30))
                compositePageTransformer.addTransformer { page, position ->
                    val r = 1 - Math.abs(position)
                    page.scaleY = 0.85f + r * 0.15f
                }
                viewPagerTrending.setPageTransformer(compositePageTransformer)
            }
        } else {
            val genre = mapMovieCategories.keys.toList()[position]
            (holderMovieByGenre as MovieByGenreViewHolder).mBinding.apply {
                tvCategoryName.text = genre.name
                rcvMovie.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                val adapter = HomeCategoryMovieChildAdapter(context, mapMovieCategories[genre], childListener)
                rcvMovie.adapter = adapter
                rcvMovie.setRecycledViewPool(viewPool)
                holderMovieByGenre.mBinding.containerSeeAll.setOnClickListener {
                    listener.onClickedSeeAll(genre)
                }
            }
        }
    }

    private val listenerViewPagerSliderAdapter = object : ViewPagerSliderAdapter.Listener {
    }
}