package com.hyvu.themoviedb.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.adapter.HomeCategoryMovieAdapter
import com.hyvu.themoviedb.data.entity.Genre
import com.hyvu.themoviedb.data.entity.MovieDetail
import com.hyvu.themoviedb.databinding.FragmentMoviesHomeFragmentBinding
import com.hyvu.themoviedb.viewmodel.HomeViewModel
import com.hyvu.themoviedb.viewmodel.factory.MainViewModelFactory
import java.util.*
import javax.inject.Inject

class MoviesHomeFragment : Fragment() {

    companion object {
        const val TRENDING_MOVIE = "TRENDING_MOVIE"
    }

    private lateinit var mBinding: FragmentMoviesHomeFragmentBinding

    @Inject
    lateinit var providerFactory: MainViewModelFactory
    private val mViewModel by lazy {
        ViewModelProvider(this, providerFactory)[HomeViewModel::class.java]
    }
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as MainActivity).mainComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.fragment_movies_home_fragment, container, false)
        mBinding = FragmentMoviesHomeFragmentBinding.bind(v)
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
            val bundle = Bundle()
            bundle.putParcelable(MoviesByGenreFragment.ARG_GENRE, genre)
//            findNavController().navigate(R.id.action_moviesHomeFragment_to_moviesByGenreFragment, bundle)
            listener?.onClickedSeeAll(genre)
        }

        override fun onClickedTrending() {
            listener?.onClickTrending()
        }
    }
}