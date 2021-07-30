package com.hyvu.themoviedb.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.adapter.MovieVideosAdapter
import com.hyvu.themoviedb.data.entity.MovieVideoDetail
import com.hyvu.themoviedb.databinding.FragmentDetailBinding
import com.hyvu.themoviedb.viewmodel.DetailViewModel
import com.hyvu.themoviedb.viewmodel.factory.DetailViewModelFactory

class DetailFragment : Fragment() {

    private lateinit var mBinding: FragmentDetailBinding
    private var listener: Listener? = null
    private val mViewModel by lazy {
        ViewModelProvider(this, DetailViewModelFactory()).get(DetailViewModel::class.java)
    }

    interface Listener {
        fun onVideoClicked(movieVideoDetail: MovieVideoDetail)
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.fragment_detail, container, false)
        mBinding = FragmentDetailBinding.bind(v)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        liveData()
    }

    private fun initView() {

    }

    private fun liveData() {
        mViewModel.movieDetails.observe(viewLifecycleOwner, { movieDetails ->
            mBinding.tvContent.text = movieDetails.overview
            var companiesName = if (movieDetails.productionCompanies.isNotEmpty()) "" else "N/A"
            movieDetails.productionCompanies.forEachIndexed { index, productionCompany ->
                companiesName += productionCompany.name
                if (index != movieDetails.productionCompanies.size - 1) companiesName += " & "
            }
            mBinding.tvProductCompany.text = companiesName
        })
        mViewModel.movieVideos.observe(viewLifecycleOwner, { movieVideos ->
            mBinding.rcvMoreVideos.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = MovieVideosAdapter(context, movieVideos.movieVideoDetails, listenerMovieVideosAdapter)
            }
        })
    }

    private val listenerMovieVideosAdapter = object : MovieVideosAdapter.Listener {
        override fun onItemClicked(videoDetail: MovieVideoDetail) {
            listener?.onVideoClicked(videoDetail)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}