package com.hyvu.themoviedb.view.activity

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.hyvu.themoviedb.data.api.BASE_IMG_HIGH_QUALITY_URL
import com.hyvu.themoviedb.data.entity.Backdrop
import com.hyvu.themoviedb.databinding.ActivityMovieImageBinding
import com.hyvu.themoviedb.view.base.BaseActivity

class MovieImageActivity : BaseActivity() {

    companion object {
        const val ARG_BACKDROPS = "ARG_BACKDROP"
        const val ARG_SELECTED_POSITION = "ARG_SELECTED_POSITION"
    }

    private var listImages: List<Backdrop> = ArrayList()
    private var selectedImage = 0
    private lateinit var mBinding: ActivityMovieImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        listImages = intent.getParcelableArrayListExtra(ARG_BACKDROPS) ?: ArrayList()
        selectedImage = intent.getIntExtra(ARG_SELECTED_POSITION, 0)
        initView()
    }

    override fun inject() {

    }

    override fun getLayoutId(): View {
        mBinding = ActivityMovieImageBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun initView() {
        Glide.with(this).asBitmap().load(BASE_IMG_HIGH_QUALITY_URL + listImages[selectedImage].filePath).into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                mBinding.imgMain.setImageBitmap(resource)
                mBinding.imgMain.isEnabled = true
                mBinding.imgMain.setZoomEnabled(true)
            }

            override fun onLoadCleared(placeholder: Drawable?) {
            }

        })
    }

    override fun observerLiveData() {

    }
}