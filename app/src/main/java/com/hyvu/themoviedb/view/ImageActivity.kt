package com.hyvu.themoviedb.view

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.hyvu.themoviedb.R

class ImageActivity : AppCompatActivity() {

    companion object {
        const val ARG_BACKDROP = "ARG_BACKDROP"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

    }
}