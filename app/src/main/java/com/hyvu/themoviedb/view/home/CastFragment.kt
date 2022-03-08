package com.hyvu.themoviedb.view.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.view.home.adapter.MovieCreditAdapter
import com.hyvu.themoviedb.databinding.FragmentCastBinding
import com.hyvu.themoviedb.view.base.BaseFragment
import com.hyvu.themoviedb.viewmodel.home.MovieInfoViewModel
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
        movieId = mViewModel.currentMovie!!.movieId
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