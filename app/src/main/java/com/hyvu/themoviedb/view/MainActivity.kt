package com.hyvu.themoviedb.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.adapter.ViewPagerMainAdapter
import com.hyvu.themoviedb.data.entity.MovieDetail
import com.hyvu.themoviedb.data.repository.MovieRepository
import com.hyvu.themoviedb.databinding.ActivityMainBinding
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
    private var ytbPlayer: YouTubePlayer? = null
    var currentMovie: MovieDetail? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = mBinding.root
        setContentView(view)
        initView()
        liveData()
    }

    private fun liveData() {

    }

    private fun initView() {
        initMotionLayout()
        initTabLayoutMain()
        initYoutube()
        mBinding.tvTitle.isSelected = true
        mBinding.viewPagerContainer.isUserInputEnabled = false
        mBinding.btnClose.setOnClickListener {
            ytbPlayer?.pause()
            mBinding.motionLayout.transitionToState(R.id.hide)
        }
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

    fun showMovieDetails(movieDetail: MovieDetail) {
        this.currentMovie = movieDetail
        supportFragmentManager.beginTransaction().replace(R.id.detail_container, MovieInfoFragment.newInstance(movieDetail)).commit()
        mBinding.tvTitle.text = movieDetail.title
        mBinding.motionLayout.transitionToState(R.id.show)
    }

    fun showComment(movieDetail: MovieDetail) {
        supportFragmentManager.beginTransaction().add(R.id.detail_container, CommentFragment.newInstance(movieDetail)).addToBackStack(CommentFragment::class.java.simpleName).commit()
    }

    fun loadYtbVideo(key: String) {
        ytbPlayer?.loadVideo(key, 0f)
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