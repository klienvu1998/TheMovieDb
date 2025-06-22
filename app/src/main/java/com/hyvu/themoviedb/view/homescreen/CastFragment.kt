package com.hyvu.themoviedb.view.homescreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hyvu.themoviedb.databinding.FragmentCastBinding
import com.hyvu.themoviedb.view.base.BaseFragment
import com.hyvu.themoviedb.view.homescreen.adapter.MovieCreditAdapter
import com.hyvu.themoviedb.viewmodel.factory.MainViewModelFactory
import com.hyvu.themoviedb.viewmodel.home.MovieInfoViewModel
import java.lang.ref.WeakReference
import javax.inject.Inject

class CastFragment : BaseFragment<FragmentCastBinding>() {

    private var movieId = 0
    @Inject
    lateinit var providerFactory: MainViewModelFactory
    private val mViewModel by lazy {
        ViewModelProvider(this, providerFactory)[MovieInfoViewModel::class.java]
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCastBinding {
        return FragmentCastBinding.inflate(inflater, container, false)
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

    override fun initView() {

    }

    override fun observerLiveData() {
        mViewModel.movieCredits.observe(this, { credits ->
            mBinding.rcvCast.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = MovieCreditAdapter(WeakReference(context), credits)
            }
        })
    }

}