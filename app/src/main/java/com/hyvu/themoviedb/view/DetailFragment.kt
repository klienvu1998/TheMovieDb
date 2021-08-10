package com.hyvu.themoviedb.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.adapter.MovieImagesAdapter
import com.hyvu.themoviedb.adapter.MovieVideosAdapter
import com.hyvu.themoviedb.data.entity.Backdrop
import com.hyvu.themoviedb.data.entity.MovieDetail
import com.hyvu.themoviedb.data.entity.MovieVideoDetail
import com.hyvu.themoviedb.databinding.FragmentDetailBinding
import com.hyvu.themoviedb.viewmodel.HomeViewModel
import com.hyvu.themoviedb.viewmodel.MovieInfoViewModel
import com.hyvu.themoviedb.viewmodel.factory.MainViewModelFactory
import javax.inject.Inject

class DetailFragment : Fragment() {

    private lateinit var mBinding: FragmentDetailBinding
    @Inject
    lateinit var providerFactory: MainViewModelFactory
    private val mViewModel by lazy {
        ViewModelProvider(this, providerFactory)[MovieInfoViewModel::class.java]
    }
    private lateinit var movieDetail: MovieDetail
    private var movieVideosAdapter: MovieVideosAdapter? = null
    private var movieImagesAdapter: MovieImagesAdapter? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as MainActivity).mainComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.fragment_detail, container, false)
        mBinding = FragmentDetailBinding.bind(v)
        movieDetail = (context as MainActivity).currentMovie!!
        getData()
        return mBinding.root
    }

    private fun getData() {
        mViewModel.fetchMovieImages(movieDetail.id)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        liveData()
    }

    private fun initView() {
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

    private fun liveData() {
        mViewModel.movieFullDetails.observe(viewLifecycleOwner, { movieDetails ->
            mBinding.tvContent.text = movieDetails.overview
            var companiesName = if (movieDetails.productionCompanies.isNotEmpty()) "" else "N/A"
            movieDetails.productionCompanies.forEachIndexed { index, productionCompany ->
                companiesName += productionCompany.name
                if (index != movieDetails.productionCompanies.size - 1) companiesName += " & "
            }
            mBinding.tvProductCompany.text = companiesName
        })
        mViewModel.movieVideos.observe(viewLifecycleOwner, { movieVideos ->
            movieVideosAdapter?.updateData(movieVideos.movieVideoDetails)
        })
        mViewModel.movieImages.observe(viewLifecycleOwner, { movieImages ->
            movieImagesAdapter?.updateData(movieImages.backdrops)
        })
    }

    private val listenerMovieVideosAdapter = object : MovieVideosAdapter.Listener {
        override fun onItemClicked(videoDetail: MovieVideoDetail) {
            (activity as MainActivity).loadYtbVideo(videoDetail.key)
        }
    }

    private val listenerMovieImagesAdapter = object : MovieImagesAdapter.Listener {
        override fun onImageClicked(backdrop: Backdrop) {
            val intent = Intent(activity, ImageActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(ImageActivity.ARG_BACKDROP, backdrop)
            intent.putExtras(bundle)
            startActivity(intent)
        }

    }
}