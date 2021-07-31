package com.hyvu.themoviedb.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.adapter.HomeCategoryMovieAdapter
import com.hyvu.themoviedb.adapter.ViewPagerSliderAdapter
import com.hyvu.themoviedb.data.entity.Genre
import com.hyvu.themoviedb.data.entity.MovieDetail
import com.hyvu.themoviedb.data.entity.MoviesByGenre
import com.hyvu.themoviedb.data.entity.TrendingMovie
import com.hyvu.themoviedb.databinding.FragmentMoviesHomeFragmetBinding
import com.hyvu.themoviedb.viewmodel.HomeViewModel
import java.util.*

class MoviesHomeFragment : Fragment() {

    companion object {
        const val TRENDING_MOVIE = "TRENDING_MOVIE"
    }

    private lateinit var mBinding: FragmentMoviesHomeFragmetBinding
    private val mViewModel by viewModels<HomeViewModel>()
    private var adapterHomeCategoryMovie: HomeCategoryMovieAdapter? = null
    private val listMovieCategory: LinkedHashMap<Genre, List<MovieDetail>> = LinkedHashMap()

    interface Listener {
        fun onClickedSeeAll(genre: Genre)
        fun onClickTrending()
    }

    private var listener: Listener? = null

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.fragment_movies_home_fragmet, container, false)
        mBinding = FragmentMoviesHomeFragmetBinding.bind(v)
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
            mViewModel.fetchTrendingMovies()
        }
    }

    private fun initView() {
        initListOverviewMovie()
    }

    private fun initListOverviewMovie() {
        adapterHomeCategoryMovie = HomeCategoryMovieAdapter(context, listenerHomeCategoryAdapter, LinkedHashMap())
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mBinding.rcvCategory.layoutManager = linearLayoutManager
        mBinding.rcvCategory.adapter = adapterHomeCategoryMovie
    }

    private fun liveData() {
        mViewModel.genres.observe(viewLifecycleOwner, { genre ->
        })
        mViewModel.listOverviewMovies.observe(viewLifecycleOwner, { data ->
            mBinding.progressBar.visibility = View.GONE
            data.forEach {
                listMovieCategory[it.key] = it.value
            }
            adapterHomeCategoryMovie?.setMovieCategoryData(data)
        })
        mViewModel.listTrendingMovies.observe(viewLifecycleOwner, {trendingMovies ->
            if (trendingMovies != null && trendingMovies.trendingMovies.isNotEmpty()) {
                mViewModel.fetchHomeListMovieByGenres()
                listMovieCategory[Genre(-1, TRENDING_MOVIE)] = trendingMovies.trendingMovies.map { it.convertToMovieDetail() }
            }
        })
    }

    private val listenerHomeCategoryAdapter = object : HomeCategoryMovieAdapter.Listener {
        override fun onClickedSeeAll(genre: Genre) {
            listener?.onClickedSeeAll(genre)
        }

        override fun onClickedMovie(movieId: Int) {
            (activity as MainActivity).showMovieDetails(movieId)
        }

        override fun onClickedTrending() {
            listener?.onClickTrending()
        }
    }
}