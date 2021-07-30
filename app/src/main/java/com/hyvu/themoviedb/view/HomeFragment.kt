package com.hyvu.themoviedb.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.adapter.HomeCategoryMovieAdapter
import com.hyvu.themoviedb.adapter.ViewPagerSliderAdapter
import com.hyvu.themoviedb.data.entity.TrendingMovie
import com.hyvu.themoviedb.databinding.FragmentHomeBinding
import com.hyvu.themoviedb.viewmodel.HomeViewModel
import com.hyvu.themoviedb.viewmodel.factory.HomeViewModelFactory
import java.util.*

class HomeFragment : Fragment() {

    private val mViewModel by lazy {
        ViewModelProvider(this, HomeViewModelFactory()).get(HomeViewModel::class.java)
    }
    private lateinit var mBinding: FragmentHomeBinding
    private var adapterHomeCategoryMovie: HomeCategoryMovieAdapter? = null
    private var adapterViewPagerSlider: ViewPagerSliderAdapter? = null

    interface Listener {
        fun showMovieDetails(movieId: Int)
    }

    private var listener: Listener? = null

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val v = inflater.inflate(R.layout.fragment_home, container, false)
        mBinding = FragmentHomeBinding.bind(v)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        liveData()
        getData()
    }

    private fun getData() {
        if (mViewModel.listOverviewMovies.value == null) {
            mViewModel.fetchListMovieByGenres()
            mViewModel.fetchTrendingMovies()
        }
    }

    private fun initView() {
        initTrendingViewPager()
        initListOverviewMovie()
    }

    private fun initTrendingViewPager() {
        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(40))
        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - Math.abs(position)
            page.scaleY = 0.85f + r * 0.15f
        }
        mBinding.viewPagerTrending.setPageTransformer(compositePageTransformer)
    }

    private fun initListOverviewMovie() {
        adapterHomeCategoryMovie = HomeCategoryMovieAdapter(context, listenerHomeCategoryAdapter)
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mBinding.rcvCategory.layoutManager = linearLayoutManager
        mBinding.rcvCategory.adapter = adapterHomeCategoryMovie
    }

    private fun liveData() {
        mViewModel.genres.observe(viewLifecycleOwner, { genre ->
        })
        mViewModel.listOverviewMovies.observe(viewLifecycleOwner, { data ->
            mBinding.progressBar.visibility = View.GONE
            adapterHomeCategoryMovie?.setAdapterData(data)
        })
        mViewModel.listTrendingMovies.observe(viewLifecycleOwner, {trendingMovies ->
            if (trendingMovies != null && trendingMovies.trendingMovies.isNotEmpty()) {
                adapterViewPagerSlider = ViewPagerSliderAdapter(context, trendingMovies.trendingMovies.filterTo(
                    LinkedList<TrendingMovie>(), { movie -> movie.backdropPath != null && movie.backdropPath.isNotEmpty() }), listenerViewPagerSliderAdapter
                )
                mBinding.viewPagerTrending.apply {
                    adapter = adapterViewPagerSlider
                    clipToPadding = false
                    clipChildren = false
                    offscreenPageLimit = 3
                    getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
                }
            }
        })
    }

    private val listenerViewPagerSliderAdapter = object : ViewPagerSliderAdapter.Listener {
        override fun onTrendingMovieClicked(movieId: Int) {
            listener?.showMovieDetails(movieId)
        }
    }

    private val listenerHomeCategoryAdapter = object : HomeCategoryMovieAdapter.Listener {
        override fun onClickedSeeAll() {
        }

        override fun onClickedMovie(movieId: Int) {
            listener?.showMovieDetails(movieId)
        }
    }
}