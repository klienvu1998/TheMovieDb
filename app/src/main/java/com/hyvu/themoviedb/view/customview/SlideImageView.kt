package com.hyvu.themoviedb.view.customview

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.content.Context
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.utils.getDisplayMetrics

class SlideImageView: FrameLayout {

    private var mLandingImageView: ArrayList<ImageView> = ArrayList()
    private val defaultBackground = ImageView(context).apply {
        setBackgroundResource(R.drawable.login_bg1)
        scaleType = ImageView.ScaleType.FIT_XY
        translationX = -100f
    }
    private var anim: ObjectAnimator? = null
    private var mAnimationStatus: AnimationStatus? = null
    private var mAnimationTimer: CountDownTimer? = null
    private var timeRemaining: Long = 0
    private var currentAnimationIndex = 0
    private var nextAnimationIndex: Int = 0
    private var mAnimationSet: AnimatorSet? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    fun setData(backgroundIds: List<Int>) {
        removeAllViews()

        if (backgroundIds.isEmpty()) {
            addView(defaultBackground)
            invalidate()
            return
        }

        mLandingImageView.clear()

        mLandingImageView.addAll(
            backgroundIds.map {
                ImageView(context).apply {
                    layoutParams = LayoutParams(context.getDisplayMetrics().widthPixels * 2, LayoutParams.MATCH_PARENT)
                    scaleType =  ImageView.ScaleType.CENTER_CROP
                    alpha = 0f
                    translationX = -100f
                    setImageResource(it)
                }.also {
                    addView(it)
                }
            }
        )

        // visible first
        mLandingImageView[0].alpha = 1f

    }



    private fun animateTranslation(
        view: View,
        duration: Long,
        interpolator: TimeInterpolator,
        start: Boolean
    ) {
        val displayMetrics = context.getDisplayMetrics()
        val deviceWidth = displayMetrics.widthPixels
        val backgroundWidth = 455
        val moveX = if (deviceWidth < backgroundWidth) deviceWidth * 1 else backgroundWidth * 1
        anim = ObjectAnimator.ofFloat(view, "translationX", (moveX * -1).toFloat())
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
                currentAnimationIndex = ++ currentAnimationIndex % (mLandingImageView.size)
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
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                fadeOutTarget.visibility = View.GONE
            }
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        fadeOut.interpolator = LinearInterpolator()

        val fadeIn = ObjectAnimator.ofFloat(fadeInTarget, View.ALPHA, 0f, 1f)
        fadeIn.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                fadeInTarget.visibility = View.VISIBLE
            }
            override fun onAnimationEnd(animation: Animator) {}
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
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
                nextAnimationIndex %= mLandingImageView.size
            }
        }

        override fun onAnimationCancel(animator: Animator) {}
        override fun onAnimationRepeat(animator: Animator) {}
    }

    fun startAnimation() {
        mAnimationStatus = AnimationStatus.READY
        animateTranslation(mLandingImageView[currentAnimationIndex], 8500, AccelerateInterpolator(), true)
    }

    private enum class AnimationStatus {
        READY, RUNNING, PAUSE, STOP
    }
}