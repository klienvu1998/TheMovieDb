package com.hyvu.themoviedb.view.home.adapter

import android.content.Context
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.data.remote.entity.*
import com.hyvu.themoviedb.databinding.ItemBannerBinding
import com.hyvu.themoviedb.databinding.ItemCategoryBinding
import com.hyvu.themoviedb.view.home.HomeFragment
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap
import kotlin.math.abs

class HomeCategoryMovieAdapter(
    private val weakContext: WeakReference<Context>,
    private val listener: Listener,
    private var mapMovieCategories: LinkedHashMap<Genre, List<MovieDetail>>,
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val context = weakContext.get()
    private val viewPool = RecyclerView.RecycledViewPool()
    private val scrollStates = hashMapOf<Genre, Parcelable?>()

    companion object {
        const val TRENDING_CATEGORY = 0
        const val MOVIE_BY_GENRE = 1
    }

    interface Listener {
        fun onClickedSeeAll(genre: Genre)
        fun onClickedTrending()
    }

    fun addMovieData(data: Map<Genre, List<MovieDetail>>) {
        this.mapMovieCategories = data as LinkedHashMap<Genre, List<MovieDetail>>
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (mapMovieCategories.keys.toList()[position].name == HomeFragment.TRENDING_MOVIE) TRENDING_CATEGORY else MOVIE_BY_GENRE
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

    override fun onBindViewHolder(holderMovieByGenre: RecyclerView.ViewHolder, position: Int) {
        val key = ArrayList(mapMovieCategories.keys)[position]
        val state = scrollStates.get(key = key)
        if (holderMovieByGenre is TrendingMovieViewHolder) {
            val genre = mapMovieCategories.keys.toList()[0]
            holderMovieByGenre.mBinding.apply {
                containerSeeAll.setOnClickListener {
//                    listener.onClickedTrending()
                }
                val adapterViewPagerSlider = ViewPagerSliderAdapter(WeakReference(context), mapMovieCategories[genre]?.filterTo(
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
                    val r = 1 - abs(position)
                    page.scaleY = 0.85f + r * 0.15f
                }
                viewPagerTrending.setPageTransformer(compositePageTransformer)
            }
        } else {
            val genre = mapMovieCategories.keys.toList()[position]
            (holderMovieByGenre as MovieByGenreViewHolder).mBinding.apply {
                tvCategoryName.text = genre.name
                val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                if (state != null) {
                    layoutManager.onRestoreInstanceState(state)
                } else {
                    layoutManager.scrollToPosition(0)
                }
                rcvMovie.layoutManager = layoutManager
                val adapter = HomeCategoryMovieChildAdapter(WeakReference(context), mapMovieCategories[genre])
                adapter.stateRestorationPolicy = StateRestorationPolicy.PREVENT_WHEN_EMPTY
                rcvMovie.adapter = adapter
                rcvMovie.setRecycledViewPool(viewPool)
                holderMovieByGenre.mBinding.containerSeeAll.setOnClickListener {
                    listener.onClickedSeeAll(genre)
                }
                // save state change
                rcvMovie.setOnScrollChangeListener { _, _, _, _, _ ->
                    scrollStates[key] = layoutManager.onSaveInstanceState()
                }
            }
        }
    }

    private val listenerViewPagerSliderAdapter = object : ViewPagerSliderAdapter.Listener {
    }
}