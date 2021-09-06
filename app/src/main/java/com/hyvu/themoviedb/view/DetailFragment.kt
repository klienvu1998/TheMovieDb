package com.hyvu.themoviedb.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.adapter.MovieImagesAdapter
import com.hyvu.themoviedb.adapter.MovieVideosAdapter
import com.hyvu.themoviedb.data.entity.Backdrop
import com.hyvu.themoviedb.data.entity.MovieDetail
import com.hyvu.themoviedb.data.entity.MovieVideoDetail
import com.hyvu.themoviedb.databinding.FragmentDetailBinding
import com.hyvu.themoviedb.view.activity.MainActivity
import com.hyvu.themoviedb.view.activity.MovieImageActivity
import com.hyvu.themoviedb.view.base.BaseFragment
import com.hyvu.themoviedb.viewmodel.MovieInfoViewModel
import com.hyvu.themoviedb.viewmodel.factory.MainViewModelFactory
import java.util.ArrayList
import javax.inject.Inject

class DetailFragment : BaseFragment() {

    private lateinit var mBinding: FragmentDetailBinding
    @Inject
    lateinit var providerFactory: MainViewModelFactory
    private val mViewModel by lazy {
        ViewModelProvider(this, providerFactory)[MovieInfoViewModel::class.java]
    }
    private lateinit var movieDetail: MovieDetail
    private var movieVideosAdapter: MovieVideosAdapter? = null
    private var movieImagesAdapter: MovieImagesAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.fragment_detail, container, false)
        mBinding = FragmentDetailBinding.bind(v)
        return mBinding.root
    }

    override fun fetchData() {
        mViewModel.fetchMovieImages(movieDetail.id)
    }

    override fun inject() {
        (activity as MainActivity).mainComponent.inject(this)
    }

    override fun getBundle() {
        movieDetail = (activity as MainActivity).currentMovie!!
    }

    override fun initView() {
        mBinding.rcvMoreVideos.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            movieVideosAdapter = MovieVideosAdapter(context, listenerMovieVideosAdapter)
            adapter = movieVideosAdapter
        }
        mBinding.rcvMoreImages.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            movieImagesAdapter = MovieImagesAdapter(context, listenerMovieImagesAdapter)
            adapter = movieImagesAdapter
        }
    }

    override fun observerLiveData() {
        mViewModel.movieFullDetails.observe(this, { movieDetails ->
            mBinding.tvContent.text = movieDetails.overview
            var companiesName = if (movieDetails.productionCompanies.isNotEmpty()) "" else "N/A"
            movieDetails.productionCompanies.forEachIndexed { index, productionCompany ->
                companiesName += productionCompany.name
                if (index != movieDetails.productionCompanies.size - 1) companiesName += " & "
            }
            mBinding.tvProductCompany.text = companiesName
        })
        mViewModel.movieVideos.observe(this, { movieVideos ->
            movieVideosAdapter?.updateData(movieVideos.movieVideoDetails)
        })
        mViewModel.movieImages.observe(this, { movieImages ->
            movieImagesAdapter?.updateData(movieImages.backdrops)
        })
    }

    private val listenerMovieVideosAdapter = object : MovieVideosAdapter.Listener {
        override fun onItemClicked(videoDetail: MovieVideoDetail) {
            (activity as MainActivity).loadYtbVideo(videoDetail.key)
        }
    }

    private val listenerMovieImagesAdapter = object : MovieImagesAdapter.Listener {
        override fun onImageClicked(backdrops: List<Backdrop>, position: Int) {
            val intent = Intent(activity, MovieImageActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelableArrayList(MovieImageActivity.ARG_BACKDROPS, backdrops as ArrayList<Backdrop>)
            bundle.putInt(MovieImageActivity.ARG_SELECTED_POSITION, position)
            intent.putExtras(bundle)
            startActivity(intent)
        }

    }
}