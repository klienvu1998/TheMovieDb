package com.hyvu.themoviedb.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.adapter.HomeCategoryMovieAdapter
import com.hyvu.themoviedb.data.entity.Genre
import com.hyvu.themoviedb.data.entity.MovieDetail
import com.hyvu.themoviedb.databinding.FragmentMoviesHomeFragmentBinding
import com.hyvu.themoviedb.utils.Constraints
import com.hyvu.themoviedb.view.activity.MainActivity
import com.hyvu.themoviedb.view.base.BaseFragment
import com.hyvu.themoviedb.viewmodel.HomeViewModel
import com.hyvu.themoviedb.viewmodel.factory.MainViewModelFactory
import java.util.*
import javax.inject.Inject

class MoviesHomeFragment : BaseFragment() {

    companion object {
        const val TRENDING_MOVIE = "TRENDING_MOVIE"
    }

    private lateinit var mBinding: FragmentMoviesHomeFragmentBinding

    @Inject
    lateinit var providerFactory: MainViewModelFactory
    private val mViewModel by lazy {
        ViewModelProvider(requireActivity(), providerFactory)[HomeViewModel::class.java]
    }
    private var adapterHomeCategoryMovie: HomeCategoryMovieAdapter? = null
    private val listMovieCategory: LinkedHashMap<Genre, List<MovieDetail>> = LinkedHashMap()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.fragment_movies_home_fragment, container, false)
        mBinding = FragmentMoviesHomeFragmentBinding.bind(v)
        return mBinding.root
    }

    override fun inject() {
        (activity as MainActivity).mainComponent.inject(this)
    }

    override fun getBundle() {

    }

    override fun fetchData() {
        mViewModel.fetchTrendingMovies()
    }

    override fun initView() {
        mBinding.rcvCategory.apply {
            adapterHomeCategoryMovie = HomeCategoryMovieAdapter(context, listenerHomeCategoryAdapter, LinkedHashMap())
            val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            layoutManager = linearLayoutManager
            adapter = adapterHomeCategoryMovie
        }
    }

    override fun observerLiveData() {
        mViewModel.genres.observe(viewLifecycleOwner, {
        })
        mViewModel.listOverviewMovies.observe(viewLifecycleOwner, { data ->
            mBinding.progressBar.visibility = View.GONE
            listMovieCategory[data.first] = data.second
            adapterHomeCategoryMovie?.addMovieData(data)
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
            parentFragment?.childFragmentManager?.beginTransaction()
                    ?.add(R.id.home_container, MoviesByGenreFragment.newInstance(genre))
                    ?.addToBackStack(MoviesByGenreFragment::class.java.simpleName)
                    ?.commit()
        }

        override fun onClickedTrending() {
            parentFragment?.childFragmentManager?.beginTransaction()
                    ?.add(R.id.home_container, MoviesByGenreFragment.newInstance(Genre(-1, Constraints.TRENDING_MOVIE)))
                    ?.addToBackStack(MoviesByGenreFragment::class.java.simpleName)
                    ?.commit()
        }
    }
}