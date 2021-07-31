package com.hyvu.themoviedb.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayoutMediator
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.adapter.GenresAdapter
import com.hyvu.themoviedb.adapter.ViewPagerMainAdapter
import com.hyvu.themoviedb.data.api.BASE_IMG_LOW_QUALITY_URL
import com.hyvu.themoviedb.data.entity.MovieFullDetails
import com.hyvu.themoviedb.data.repository.MovieRepository
import com.hyvu.themoviedb.databinding.ActivityMainBinding
import com.hyvu.themoviedb.utils.Utils
import com.hyvu.themoviedb.view.*
import com.hyvu.themoviedb.viewmodel.MainViewModel
import com.hyvu.themoviedb.viewmodel.factory.MainViewModelFactory
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener


class MainActivity : AppCompatActivity() {

    val mViewModel by lazy {
        ViewModelProvider(this, MainViewModelFactory()).get(MainViewModel::class.java)
    }
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var viewPagerMainMainAdapter: ViewPagerMainAdapter
    private lateinit var viewPagerDetailMainAdapter: ViewPagerMainAdapter
    private var ytbPlayer: YouTubePlayer? = null
    private var movieId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = mBinding.root
        setContentView(view)
        initView()
        liveData()
    }

    private fun liveData() {
        mViewModel.movieFullDetails.observe(this, { movieDetails ->
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
        viewPagerDetailMainAdapter = ViewPagerMainAdapter(this, listenerViewDetailPagerAdapter)
        mBinding.detailContainer.viewPagerDetail.adapter = viewPagerDetailMainAdapter
        TabLayoutMediator(mBinding.detailContainer.tabLayoutDetail, mBinding.detailContainer.viewPagerDetail) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Details"
                }
                1 -> {
                    tab.text = "Cast"
                }
                2 -> {
                    tab.text = "Comment"
                }
            }
        }.attach()
    }

    private fun initTabLayoutMain() {
        viewPagerMainMainAdapter = ViewPagerMainAdapter(this, listenerViewMainPagerAdapter)
        mBinding.viewPagerContainer.adapter = viewPagerMainMainAdapter
        TabLayoutMediator(mBinding.tabLayout, mBinding.viewPagerContainer) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Home"
                }
                1 -> {
                    tab.text = "TikMovie"
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
                        ytbPlayer?.pause()
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

    private val listenerViewMainPagerAdapter = object : ViewPagerMainAdapter.Listener {
        override fun onCreateFragment(position: Int): Fragment {
            var fragment: Fragment = HomeFragment()
            when (position) {
                0 -> {
                    fragment = HomeFragment()
                }
                1 -> {
                    fragment = TikMovieFragment()
                }
                2 -> {
                    fragment = UserFragment()
                }
            }
            return fragment
        }
    }

    fun showMovieDetails(movieId: Int) {
        mBinding.detailContainer.progressBarLoadingDetail.visibility = View.VISIBLE
        this.movieId = movieId
        mViewModel.fetchMovieDetails(movieId)
        mBinding.motionLayout.transitionToState(R.id.show)
    }

    private val listenerViewDetailPagerAdapter = object : ViewPagerMainAdapter.Listener {
        override fun onCreateFragment(position: Int): Fragment {
            var fragment: Fragment = DetailFragment()
            when (position) {
                0 -> {
                    fragment = DetailFragment()
                }
                1 -> {
                    fragment = CastFragment.newInstance(movieId)
                }
                2 -> {
                    fragment = UserFragment()
                }
            }
            return fragment
        }
    }

    fun loadYtbVideo(key: String) {
        ytbPlayer?.loadVideo(key, 0f)
    }

    private fun setupLayoutForDetailScreen(movieFullDetails: MovieFullDetails) {
        mBinding.detailContainer.progressBarLoadingDetail.visibility = View.GONE
        mBinding.tvTitle.text = movieFullDetails.title
        mBinding.detailContainer.posterContainer.apply {
            tvTitle.text = movieFullDetails.title
            tvTitle.isSelected = true
            ratingBar.max = 10
            ratingBar.rating = ((movieFullDetails.voteAverage * 5) / 10).toFloat()
            tvVoteCount.text = movieFullDetails.voteCount.toString()
            tvReleaseDate.text = movieFullDetails.releaseDate
            Utils.loadGlideImage(this@MainActivity, BASE_IMG_LOW_QUALITY_URL, movieFullDetails.posterPath, imgPoster)
            rcvGenres.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = GenresAdapter(context, movieFullDetails.genres)
            }
        }
        if (!movieFullDetails.spokenLanguages.isNullOrEmpty()) mBinding.detailContainer.tvSpokenLanguage.text = movieFullDetails.spokenLanguages[0].iso6391.toUpperCase()
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