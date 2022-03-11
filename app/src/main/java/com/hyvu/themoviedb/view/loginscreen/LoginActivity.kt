package com.hyvu.themoviedb.view.loginscreen

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.CountDownTimer
import android.util.DisplayMetrics
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.text.TextUtilsCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.ViewModelProvider
import com.hyvu.themoviedb.MyApplication
import com.hyvu.themoviedb.data.remote.api.TheMovieDbClient
import com.hyvu.themoviedb.databinding.ActivityLoginBinding
import com.hyvu.themoviedb.di.LoginComponent
import com.hyvu.themoviedb.utils.UserManager
import com.hyvu.themoviedb.view.homescreen.MainActivity
import com.hyvu.themoviedb.view.base.BaseActivity
import com.hyvu.themoviedb.viewmodel.login.LoginViewModel
import com.hyvu.themoviedb.viewmodel.factory.MainViewModelFactory
import javax.inject.Inject

class LoginActivity : BaseActivity() {

    companion object {
        @SuppressLint("ConstantLocale")
        val IS_RTL_LANGUAGE = TextUtilsCompat.getLayoutDirectionFromLocale(java.util.Locale.getDefault()) == ViewCompat.LAYOUT_DIRECTION_RTL
    }

    @Inject
    lateinit var providerFactory: MainViewModelFactory

    private val userManager: UserManager by lazy {
        (application as MyApplication).userManager
    }

    private val mViewModel by lazy {
        ViewModelProvider(this, providerFactory)[LoginViewModel::class.java]
    }

    lateinit var loginComponent: LoginComponent
    private lateinit var mBinding: ActivityLoginBinding
    private var mLandingImageView: ArrayList<ImageView> = ArrayList()
    private var anim: ObjectAnimator? = null
    private var mAnimationStatus: AnimationStatus? = null
    private var mAnimationTimer: CountDownTimer? = null
    private var timeRemaining: Long = 0
    private var currentAnimationIndex = 0
    private  var nextAnimationIndex:Int = 0
    private var mAnimationSet: AnimatorSet? = null

    override fun getBundle() {
        val sessionId = userManager.sessionId
        if (sessionId.isNotEmpty()) {
            startMainActivity()
        }
    }

    override fun fetchData() {

    }

    override fun inject() {
        loginComponent = (application as MyApplication).appComponent.loginComponent().create()
        loginComponent.inject(this)
    }

    override fun getLayoutId(): View {
        mBinding = ActivityLoginBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun initView() {
        mLandingImageView.add(mBinding.backgroundOne)
        mLandingImageView.add(mBinding.backgroundTwo)
        mLandingImageView.add(mBinding.backgroundThree)
        mBinding.btnLogin.setOnClickListener {
//            val intent = Intent(this, LoginWebView::class.java)
//            startActivity(intent)
            mViewModel.fetchAuthenticateToken()
        }
        mBinding.btnLoginGuest.setOnClickListener {
            startMainActivity()
        }
        startAnimation()
    }

    private fun startAnimation() {
        mAnimationStatus = AnimationStatus.READY
        animateTranslation(mLandingImageView[currentAnimationIndex], 8500, AccelerateInterpolator(), true)
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private val intentResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == LoginWebViewActivity.RESULT_CODE) {
            val requestToken = it.data?.extras?.get("request_token") as String
            val approved = it.data?.extras?.get("approved") as Boolean
            if (approved) mViewModel.fetchSession(requestToken)
            else {

            }
        }
    }

    override fun observerLiveData() {
        mViewModel.authenticateToken.observe(this, { authenticateToken ->
            val intent = Intent(this, LoginWebViewActivity::class.java)
            intent.putExtra("url", TheMovieDbClient.getAuthenticateDeepLink(authenticateToken))
            intentResult.launch(intent)
        })
        mViewModel.session.observe(this, { session ->
            if (session.success) {
                userManager.saveSessionId(session.sessionId)
                startMainActivity()
            }
        })
    }

    private fun animateTranslation(
        view: View,
        duration: Long,
        interpolator: TimeInterpolator,
        start: Boolean
    ) {
        val rtl = if (IS_RTL_LANGUAGE) 1 else -1
        val displayMetrics = DisplayMetrics()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            display?.getRealMetrics(displayMetrics)
        } else {
            @Suppress("DEPRECATION")
            windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        }
        val deviceWidth = displayMetrics.widthPixels
        val backgroundWidth = 455
        val moveX = if (deviceWidth < backgroundWidth) deviceWidth * 1 else backgroundWidth * 1
        anim = ObjectAnimator.ofFloat(view, "translationX", (moveX * rtl).toFloat())
        anim?.duration = duration
        anim?.interpolator = interpolator
        anim?.addListener(mAnimationListener)
        anim?.startDelay = 0
        anim?.start()

        val counter: Long = if (start) 7400 else 7600
        startCountTimer(counter)
        mAnimationStatus = AnimationStatus.RUNNING
    }

    private fun startCountTimer(counter: Long) {
        mAnimationTimer = object : CountDownTimer(counter, 100) {
            override fun onTick(millisUntilFinished: Long) {
                if (mAnimationStatus == AnimationStatus.PAUSE) cancel()
                else timeRemaining = 1
            }

            override fun onFinish() {
                timeRemaining = 0
                val fadeOut = mLandingImageView[currentAnimationIndex]
                currentAnimationIndex = ++ currentAnimationIndex % 3
                val fadeIn = mLandingImageView[currentAnimationIndex]
                crossFadeAnimation(fadeIn, fadeOut, 1200)
                animateTranslation(mLandingImageView[currentAnimationIndex], 9000, AccelerateInterpolator(), false)
            }
        }.start()
    }

    private fun crossFadeAnimation(fadeInTarget: View, fadeOutTarget: View, duration: Long) {
        mAnimationSet = AnimatorSet()
        val fadeOut = ObjectAnimator.ofFloat(fadeOutTarget, View.ALPHA, 1f, 0f)
        fadeOut.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {}
            override fun onAnimationEnd(animation: Animator?) {
                fadeOutTarget.visibility = View.GONE
            }
            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationRepeat(animation: Animator?) {}
        })
        fadeOut.interpolator = LinearInterpolator()

        val fadeIn = ObjectAnimator.ofFloat(fadeInTarget, View.ALPHA, 0f, 1f)
        fadeIn.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
                fadeInTarget.visibility = View.VISIBLE
            }
            override fun onAnimationEnd(animation: Animator?) {}
            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationRepeat(animation: Animator?) {}
        })
        fadeIn.interpolator = LinearInterpolator()
        mAnimationSet?.duration = duration
        mAnimationSet?.playTogether(fadeIn, fadeOut)
        mAnimationSet?.start()
    }

    private val mAnimationListener: Animator.AnimatorListener = object : Animator.AnimatorListener {
        override fun onAnimationStart(animator: Animator) {}
        override fun onAnimationEnd(animator: Animator) {
            if (animator.duration >= 8000) {
                mLandingImageView[nextAnimationIndex++].x = 0f
                nextAnimationIndex %= 3
            }
        }

        override fun onAnimationCancel(animator: Animator) {}
        override fun onAnimationRepeat(animator: Animator) {}
    }

    private enum class AnimationStatus {
        READY, RUNNING, PAUSE, STOP
    }
}