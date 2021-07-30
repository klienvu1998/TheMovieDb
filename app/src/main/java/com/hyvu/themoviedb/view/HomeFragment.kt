package com.hyvu.themoviedb.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.adapter.HomeCategoryMovieAdapter
import com.hyvu.themoviedb.data.entity.MovieDetails
import com.hyvu.themoviedb.databinding.FragmentHomeBinding
import com.hyvu.themoviedb.viewmodel.HomeViewModel
import com.hyvu.themoviedb.viewmodel.factory.HomeViewModelFactory

class HomeFragment : Fragment() {

    private val mViewModel by lazy {
        ViewModelProvider(this, HomeViewModelFactory()).get(HomeViewModel::class.java)
    }
    private lateinit var mBinding: FragmentHomeBinding
    private var adapterHomeCategoryMovie: HomeCategoryMovieAdapter? = null

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
            mViewModel.fetchOverviewMovie()
        }
    }

    private fun initView() {
        initListOverviewMovie()
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
    }

    private val listenerHomeCategoryAdapter = object : HomeCategoryMovieAdapter.Listener {
        override fun onClickedSeeAll() {
        }

        override fun onClickedMovie(movieId: Int) {
            listener?.showMovieDetails(movieId)
        }

    }
}