package com.hyvu.themoviedb

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.hyvu.themoviedb.adapter.GenresAdapter
import com.hyvu.themoviedb.adapter.ViewPagerAdapter
import com.hyvu.themoviedb.data.api.BASE_IMG_URL
import com.hyvu.themoviedb.data.entity.MovieDetails
import com.hyvu.themoviedb.data.entity.MovieVideoDetail
import com.hyvu.themoviedb.data.repository.MovieRepository
import com.hyvu.themoviedb.databinding.ActivityMainBinding
import com.hyvu.themoviedb.view.DetailFragment
import com.hyvu.themoviedb.view.HomeFragment
import com.hyvu.themoviedb.view.SearchFragment
import com.hyvu.themoviedb.view.UserFragment
import com.hyvu.themoviedb.viewmodel.MainViewModel
import com.hyvu.themoviedb.viewmodel.factory.MainViewModelFactory
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener
import rx.internal.util.UtilityFunctions


class MainActivity : AppCompatActivity() {

    private val mViewModel by lazy {
        ViewModelProvider(this, MainViewModelFactory()).get(MainViewModel::class.java)
    }
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var viewPagerMainAdapter: ViewPagerAdapter
    private lateinit var viewPagerDetailAdapter: ViewPagerAdapter
    private var ytbPlayer: YouTubePlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = mBinding.root
        setContentView(view)
        initView()
        liveData()
    }

    private fun liveData() {
        mViewModel.movieDetails.observe(this, { movieDetails ->
            setupLayoutForDetailScreen(movieDetails)
            initTabLayoutDetail()
        })
    }

    private fun initView() {
        initMotionLayout()
        initTabLayoutMain()
        initYoutube()
        mBinding.tvTitle.isSelected = true
        mBinding.viewPagerContainer.isUserInputEnabled = false
        mBinding.detailContainer.viewPagerDetail.isUserInputEnabled = false
        mBinding.btnClose.setOnClickListener {
            ytbPlayer?.pause()
            mBinding.motionLayout.transitionToState(R.id.hide)
        }
    }

    private fun initTabLayoutDetail() {
        viewPagerDetailAdapter = ViewPagerAdapter(this, listenerViewDetailPagerAdapter)
        mBinding.detailContainer.viewPagerDetail.adapter = viewPagerDetailAdapter
        TabLayoutMediator(mBinding.detailContainer.tabLayoutDetail, mBinding.detailContainer.viewPagerDetail) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Details"
                }
                1 -> {
                    tab.text = "Production"
                }
                2 -> {
                    tab.text = "Comment"
                }
            }
        }.attach()
    }

    private fun initTabLayoutMain() {
        viewPagerMainAdapter = ViewPagerAdapter(this, listenerViewMainPagerAdapter)
        mBinding.viewPagerContainer.adapter = viewPagerMainAdapter
        TabLayoutMediator(mBinding.tabLayout, mBinding.viewPagerContainer) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Home"
                }
                1 -> {
                    tab.text = "Search"
                }
                2 -> {
                    tab.text = "User"
                }
            }
        }.attach()
    }

    private fun initMotionLayout() {
        mBinding.motionLayout.setTransitionListener(object: MotionLayout.TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, currentState: Int, p2: Int) {
                when (currentState) {
                    R.id.show -> isShowControllerYoutube(false)
                }
            }

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
            }

            override fun onTransitionCompleted(p0: MotionLayout?, state: Int) {
                when (state) {
                    R.id.card -> {

                    }
                    R.id.show -> {
                        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                        isShowControllerYoutube(true)
                    }
                    R.id.hide -> {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                        mBinding.detailContainer.progressBarLoadingDetail.visibility = View.VISIBLE
                    }
                }
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
            }

        })
    }

    fun isShowControllerYoutube(isShow: Boolean) {
        mBinding.youtubeView.getPlayerUiController().apply {
            showUi(isShow)
            showVideoTitle(isShow)
            showFullscreenButton(false)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initYoutube() {
        lifecycle.addObserver(mBinding.youtubeView)
//        mBinding.youtubeView.inflateCustomPlayerUi(R.layout.youtube_custom_layout)
        mBinding.youtubeView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                super.onReady(youTubePlayer)
                ytbPlayer = youTubePlayer
                mViewModel.movieVideos.observe(this@MainActivity, { movieVideos ->
                    if (movieVideos != null && movieVideos.movieVideoDetails.isNotEmpty()) {
                        this@MainActivity.ytbPlayer?.apply {
                            cueVideo(movieVideos.movieVideoDetails[0].key, 0f)
                        }
                    }
                })
            }
        })
        mBinding.youtubeView.addFullScreenListener(object : YouTubePlayerFullScreenListener {
            override fun onYouTubePlayerEnterFullScreen() {
                mBinding.motionLayout.transitionToState(R.id.ytb_fullscreen)
            }

            override fun onYouTubePlayerExitFullScreen() {
                mBinding.motionLayout.transitionToState(R.id.show)
            }

        })
    }

    private val listenerViewMainPagerAdapter = object : ViewPagerAdapter.Listener {
        override fun onCreateFragment(position: Int): Fragment {
            var fragment: Fragment = HomeFragment()
            when (position) {
                0 -> {
                    fragment = HomeFragment()
                    fragment.setListener(listenerHomeFragment)
                }
                1 -> {
                    fragment = SearchFragment()
                }
                2 -> {
                    fragment = UserFragment()
                }
            }
            return fragment
        }
    }

    private val listenerViewDetailPagerAdapter = object : ViewPagerAdapter.Listener {
        override fun onCreateFragment(position: Int): Fragment {
            var fragment: Fragment = DetailFragment()
            when (position) {
                0 -> {
                    fragment = DetailFragment()
                    fragment.setListener(listenerMovieDetailFragment)
                }
                1 -> {
                    fragment
                }
                2 -> {
                    fragment
                }
            }
            return fragment
        }
    }

    private val listenerMovieDetailFragment = object : DetailFragment.Listener {
        override fun onVideoClicked(movieVideoDetail: MovieVideoDetail) {
            ytbPlayer?.loadVideo(movieVideoDetail.key, 0f)
        }
    }

    private val listenerHomeFragment = object : HomeFragment.Listener {
        override fun showMovieDetails(movieId: Int) {
            mViewModel.fetchMovieDetails(movieId)
            mBinding.motionLayout.transitionToState(R.id.show)
        }
    }

    private fun setupLayoutForDetailScreen(movieDetails: MovieDetails) {
        mBinding.detailContainer.progressBarLoadingDetail.visibility = View.GONE
        mBinding.tvTitle.text = movieDetails.title
        mBinding.detailContainer.apply {
            tvTitle.text = movieDetails.title
            tvTitle.isSelected = true
            ratingBar.max = 10
            ratingBar.rating = ((movieDetails.voteAverage * 5) / 10).toFloat()
            tvVoteCount.text = movieDetails.voteCount.toString()
            tvReleaseDate.text = movieDetails.releaseDate
            tvSpokenLanguage.text = movieDetails.spokenLanguages[0].iso6391.toUpperCase()
            Glide.with(this@MainActivity)
                .load(BASE_IMG_URL + movieDetails.posterPath)
                .centerCrop()
                .into(imgPoster)
        }
        mBinding.detailContainer.rcvGenres.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = GenresAdapter(context, movieDetails.genres)
        }
    }

    override fun onStop() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onStop()
    }

    override fun onDestroy() {
        mBinding.youtubeView.release()
        MovieRepository.deinit()
        super.onDestroy()
    }

    override fun onBackPressed() {
        when (mBinding.motionLayout.currentState) {
            R.id.ytb_fullscreen -> {
                mBinding.motionLayout.transitionToState(R.id.show)
                return
            }
            R.id.show -> {
                mBinding.motionLayout.transitionToState(R.id.hide)
                return
            }
        }
        super.onBackPressed()
    }

}