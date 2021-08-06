package com.hyvu.themoviedb.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
import com.hyvu.themoviedb.viewmodel.MovieInfoViewModel
import com.hyvu.themoviedb.viewmodel.factory.MovieInfoViewModelFactory

class MovieInfoFragment: Fragment() {

    companion object {
        const val ARG_MOVIE_ID = "ARG_MOVIE_ID"

        fun newInstance(movieDetail: MovieDetail): MovieInfoFragment {
            val f = MovieInfoFragment()
            val arg = Bundle()
            arg.putParcelable(ARG_MOVIE_ID, movieDetail)
            f.arguments = arg
            return f
        }
    }

    private lateinit var movieDetail: MovieDetail
    private lateinit var mBinding: FragmentMovieInfoBinding
    private val mViewModel by lazy {
        ViewModelProvider(this, MovieInfoViewModelFactory()).get(MovieInfoViewModel::class.java)
    }
    private lateinit var viewPagerDetailMainAdapter: ViewPagerInfoAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.fragment_movie_info, container, false)
        mBinding = FragmentMovieInfoBinding.bind(v)
        movieDetail = arguments?.getParcelable(ARG_MOVIE_ID)!!
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.fetchMovieDetails(movieDetail.id)
        initView()
        liveData()
    }

    private fun liveData() {
        mViewModel.movieFullDetails.observe(viewLifecycleOwner, { movieDetails ->
            setupLayoutForDetailScreen(movieDetails)
            initTabLayoutDetail()
        })
    }

    private fun initView() {
        mBinding.progressBarLoadingDetail.visibility = View.VISIBLE
        mBinding.viewPagerDetail.isUserInputEnabled = false
        mBinding.btnComment.setOnClickListener {
            (context as MainActivity).showComment(movieDetail)
        }
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
                    fragment = CastFragment.newInstance(movieDetail.id)
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