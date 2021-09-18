package com.hyvu.themoviedb.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayoutMediator
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.adapter.GenresAdapter
import com.hyvu.themoviedb.adapter.ViewPagerInfoAdapter
import com.hyvu.themoviedb.data.api.BASE_IMG_LOW_QUALITY_URL
import com.hyvu.themoviedb.data.entity.MovieDetail
import com.hyvu.themoviedb.data.entity.MovieFullDetails
import com.hyvu.themoviedb.databinding.FragmentMovieInfoBinding
import com.hyvu.themoviedb.utils.Utils
import com.hyvu.themoviedb.view.activity.MainActivity
import com.hyvu.themoviedb.view.base.BaseFragment
import com.hyvu.themoviedb.viewmodel.MovieInfoViewModel
import com.hyvu.themoviedb.viewmodel.factory.MainViewModelFactory
import javax.inject.Inject

class MovieInfoFragment: BaseFragment() {
    @Inject
    lateinit var providerFactory: MainViewModelFactory

    private lateinit var movieDetail: MovieDetail
    private lateinit var mBinding: FragmentMovieInfoBinding
    private val mViewModel by lazy {
        ViewModelProvider(this, providerFactory).get(MovieInfoViewModel::class.java)
    }
    private lateinit var viewPagerDetailMainAdapter: ViewPagerInfoAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.fragment_movie_info, container, false)
        mBinding = FragmentMovieInfoBinding.bind(v)
        return mBinding.root
    }

    override fun inject() {
        (activity as MainActivity).mainComponent.inject(this)
    }

    override fun getBundle() {
        movieDetail = (activity as MainActivity).currentMovie!!
    }

    override fun fetchData() {
        mViewModel.fetchMovieDetails(movieDetail.id)
    }

    override fun initView() {
        mBinding.progressBarLoadingDetail.visibility = View.VISIBLE
        mBinding.viewPagerDetail.isUserInputEnabled = false
        mBinding.btnComment.setOnClickListener {
            (context as MainActivity).showComment()
        }
        initTabLayoutDetail()
        mViewModel.favoriteList.observe(viewLifecycleOwner, Observer {
            val movie = it.movieDetails.firstOrNull { it.id == movieDetail.id }
            if (movie != null) {
                setUpFavoriteButton(true)
                movieDetail.isFavorite = true
            } else {
                setUpFavoriteButton(false)
            }
        })
    }

    private fun setUpFavoriteButton(isFavorite: Boolean) {
        if (isFavorite) {
            mBinding.btnHeart.setImageResource(R.drawable.ic_favorite)
        } else {
            mBinding.btnHeart.setImageResource(R.drawable.ic_no_favorite)
        }
        mBinding.btnHeart.setOnClickListener {
            (activity as MainActivity).userManager.accountId?.let { it1 ->
                mViewModel.setFavorite(it1, (activity as MainActivity).userManager.sessionId, movieDetail.id, !movieDetail.isFavorite)
            }
        }
    }

    override fun observerLiveData() {
        mViewModel.movieFullDetails.observe(viewLifecycleOwner, { movieDetails ->
            setupLayoutForDetailScreen(movieDetails)
        })
        mViewModel.favorite.observe(viewLifecycleOwner, {
            if ((it.statusCode ?: -1) >= 0) {
                movieDetail.isFavorite = !movieDetail.isFavorite
                setUpFavoriteButton(movieDetail.isFavorite)
                mViewModel.updateFavoriteList(movieDetail)
            }
        })
    }

    private fun initTabLayoutDetail() {
        viewPagerDetailMainAdapter = ViewPagerInfoAdapter(this, listenerViewDetailPagerAdapter)
        mBinding.viewPagerDetail.adapter = viewPagerDetailMainAdapter
        TabLayoutMediator(mBinding.tabLayoutDetail, mBinding.viewPagerDetail) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Details"
                }
                1 -> {
                    tab.text = "Cast"
                }
            }
        }.attach()
    }

    private val listenerViewDetailPagerAdapter = object : ViewPagerInfoAdapter.Listener {
        override fun onCreateFragment(position: Int): Fragment {
            var fragment: Fragment = DetailFragment()
            when (position) {
                0 -> {
                    fragment = DetailFragment()
                }
                1 -> {
                    fragment = CastFragment()
                }
            }
            return fragment
        }
    }

    private fun setupLayoutForDetailScreen(movieFullDetails: MovieFullDetails) {
        mBinding.progressBarLoadingDetail.visibility = View.GONE
        mBinding.posterContainer.apply {
            tvTitle.text = movieFullDetails.title
            tvTitle.isSelected = true
            ratingBar.max = 10
            ratingBar.rating = ((movieFullDetails.voteAverage * 5) / 10).toFloat()
            tvVoteCount.text = movieFullDetails.voteCount.toString()
            tvReleaseDate.text = movieFullDetails.releaseDate
            Utils.loadGlideImage(context, BASE_IMG_LOW_QUALITY_URL, movieFullDetails.posterPath, imgPoster, R.drawable.ic_image_not_supported)
            rcvGenres.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = GenresAdapter(context, movieFullDetails.genres)
            }
        }
        if (!movieFullDetails.spokenLanguages.isNullOrEmpty()) mBinding.tvSpokenLanguage.text = movieFullDetails.spokenLanguages[0].iso6391.toUpperCase()
    }
}