package com.hyvu.themoviedb

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
//import com.google.android.youtube.player.YouTubeInitializationResult
//import com.google.android.youtube.player.YouTubePlayer
//import com.google.android.youtube.player.YouTubePlayerSupportFragmentX
import com.hyvu.themoviedb.adapter.ViewPagerAdapter
import com.hyvu.themoviedb.data.api.BASE_IMG_URL
import com.hyvu.themoviedb.data.entity.MovieDetails
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
        mBinding.viewPagerContainer.isUserInputEnabled = false
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
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {

            }

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
            }

            override fun onTransitionCompleted(p0: MotionLayout?, state: Int) {
                when (state) {
                    R.id.card -> {
                        mBinding.youtubeView.getPlayerUiController().apply {
                            showUi(false)
                            showVideoTitle(false)
                            showFullscreenButton(false)
                        }
                    }
                    R.id.show -> {
                        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                        mBinding.youtubeView.getPlayerUiController().apply {
                            showUi(true)
                            showVideoTitle(true)
                            showFullscreenButton(true)
                        }
                        mBinding.youtubeView.exitFullScreen()
                    }
                    R.id.hide -> {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                        mBinding.progressBarLoadingDetail.visibility = View.VISIBLE
                    }
                }
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
            }

        })
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

       /* val youtubePlayerSupportFragment = YouTubePlayerSupportFragmentX.newInstance()
        supportFragmentManager.beginTransaction().replace(R.id.youtube_view, youtubePlayerSupportFragment).commit()
        youtubePlayerSupportFragment.initialize(Constraints.YOUTUBE_API, object : YouTubePlayer.OnInitializedListener {
            override fun onInitializationSuccess(
                p0: YouTubePlayer.Provider?,
                youTubePlayer: YouTubePlayer?,
                p2: Boolean
            ) {
                this@MainActivity.ytbPlayer = youTubePlayer
                ytbPlayer?.setOnFullscreenListener {
                        mBinding.motionLayout.visibility = View.GONE
                }
            }

            override fun onInitializationFailure(
                p0: YouTubePlayer.Provider?,
                p1: YouTubeInitializationResult?
            ) {

            }
        })*/
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

    private val listenerHomeFragment = object : HomeFragment.Listener {
        override fun showMovieDetails(movieId: Int) {
            mViewModel.fetchMovieDetails(movieId)
            mBinding.motionLayout.transitionToState(R.id.show)
        }
    }

    private fun setupLayoutForDetailScreen(movieDetails: MovieDetails) {
        mBinding.progressBarLoadingDetail.visibility = View.GONE
        mBinding.tvTitle.text = movieDetails.title
        mBinding.detailContainer.apply {
            tvTitle.text = movieDetails.title
            ratingBar.max = 10
            ratingBar.rating = ((movieDetails.voteAverage * 5) / 10).toFloat()
            Glide.with(this@MainActivity)
                .load(BASE_IMG_URL + movieDetails.posterPath)
                .centerCrop()
                .into(imgPoster)
        }
    }

    override fun onDestroy() {
        mBinding.youtubeView.release()
//        ytbPlayer?.release()
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