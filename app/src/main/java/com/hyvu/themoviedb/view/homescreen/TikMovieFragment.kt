package com.hyvu.themoviedb.view.homescreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.hyvu.themoviedb.databinding.FragmentTikmovieBinding
import com.hyvu.themoviedb.view.base.BaseFragment
import com.hyvu.themoviedb.view.homescreen.adapter.TikMoviePagingDataAdapter
import com.hyvu.themoviedb.viewmodel.factory.MainViewModelFactory
import com.hyvu.themoviedb.viewmodel.home.TikMovieViewModel
import java.lang.ref.WeakReference
import javax.inject.Inject

class TikMovieFragment : BaseFragment<FragmentTikmovieBinding>() {

    @Inject
    lateinit var providerFactory: MainViewModelFactory
    private val mViewModel by lazy {
        ViewModelProvider(this, providerFactory).get(TikMovieViewModel::class.java)
    }
    private var tikMoviePagingDataAdapter: TikMoviePagingDataAdapter? = null

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTikmovieBinding {
        return FragmentTikmovieBinding.inflate(inflater, container, false)
    }

    override fun inject() {
        (activity as MainActivity).mainComponent.inject(this)
    }

    override fun getBundle() {

    }

    override fun fetchData() {
        mViewModel.fetchTikMovie()
    }

    override fun initView() {
        mBinding.rcvMovie.apply {
            tikMoviePagingDataAdapter = TikMoviePagingDataAdapter(WeakReference(context), listenerTikMovieViewPagerAdapter, mViewModel.movieGenres)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            PagerSnapHelper().attachToRecyclerView(this)
            adapter = tikMoviePagingDataAdapter
        }
    }

    override fun observerLiveData() {
        mViewModel.tikMovieDetails.observe(this, { tikMovie ->
            tikMoviePagingDataAdapter?.submitData(lifecycle, tikMovie)
        })
    }

    private val listenerTikMovieViewPagerAdapter = object : TikMoviePagingDataAdapter.Listener {

    }
}