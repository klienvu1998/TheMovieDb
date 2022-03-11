package com.hyvu.themoviedb.view.imagescreen

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.hyvu.themoviedb.MyApplication
import com.hyvu.themoviedb.data.remote.api.BASE_IMG_HIGH_QUALITY_URL
import com.hyvu.themoviedb.data.remote.entity.Backdrop
import com.hyvu.themoviedb.databinding.ActivityMovieImageBinding
import com.hyvu.themoviedb.di.MovieImageComponent
import com.hyvu.themoviedb.view.base.BaseActivity

class MovieImageActivity : BaseActivity() {

    companion object {
        const val ARG_BACKDROPS = "ARG_BACKDROP"
        const val ARG_SELECTED_POSITION = "ARG_SELECTED_POSITION"
    }

    private var listImages: List<Backdrop> = ArrayList()
    private var selectedImage = 0
    private lateinit var mBinding: ActivityMovieImageBinding
    lateinit var movieMovieImageComponent: MovieImageComponent

    override fun getBundle() {
        listImages = intent.getParcelableArrayListExtra(ARG_BACKDROPS) ?: ArrayList()
        selectedImage = intent.getIntExtra(ARG_SELECTED_POSITION, 0)
    }

    override fun fetchData() {

    }

    override fun inject() {
        movieMovieImageComponent = (application as MyApplication).appComponent.imageComponent().create()
        movieMovieImageComponent.inject(this)
    }

    override fun getLayoutId(): View {
        mBinding = ActivityMovieImageBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun initView() {
        Glide.with(this).asBitmap().load(BASE_IMG_HIGH_QUALITY_URL + listImages[selectedImage].filePath).into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                mBinding.imgMain.setImageBitmap(resource)
                mBinding.progressBar.visibility = View.GONE
                mBinding.imgMain.isEnabled = true
                mBinding.imgMain.isZoomEnabled = true
            }

            override fun onLoadCleared(placeholder: Drawable?) {
            }

        })
    }

    override fun observerLiveData() {

    }
}