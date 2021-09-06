package com.hyvu.themoviedb.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.adapter.MovieCreditAdapter
import com.hyvu.themoviedb.databinding.FragmentCastBinding
import com.hyvu.themoviedb.view.activity.MainActivity
import com.hyvu.themoviedb.view.base.BaseFragment
import com.hyvu.themoviedb.viewmodel.MovieInfoViewModel
import com.hyvu.themoviedb.viewmodel.factory.MainViewModelFactory
import javax.inject.Inject

class CastFragment : BaseFragment() {

    private lateinit var mBinding: FragmentCastBinding
    private var movieId = 0
    @Inject
    lateinit var providerFactory: MainViewModelFactory
    private val mViewModel by lazy {
        ViewModelProvider(this, providerFactory)[MovieInfoViewModel::class.java]
    }

    override fun inject() {
        (activity as MainActivity).mainComponent.inject(this)
    }

    override fun getBundle() {
        movieId = (activity as MainActivity).currentMovie?.id ?: 0
    }

    override fun fetchData() {
        mViewModel.fetchMovieCredits(movieId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.fragment_cast, container, false)
        mBinding = FragmentCastBinding.bind(v)
        return mBinding.root
    }

    override fun initView() {

    }

    override fun observerLiveData() {
        mViewModel.movieCredits.observe(this, { credits ->
            mBinding.rcvCast.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = MovieCreditAdapter(context, credits)
            }
        })
    }

}