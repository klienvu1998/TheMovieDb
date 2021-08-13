package com.hyvu.themoviedb.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.adapter.TikMoviePagingDataAdapter
import com.hyvu.themoviedb.databinding.FragmentTikmovieBinding
import com.hyvu.themoviedb.view.activity.MainActivity
import com.hyvu.themoviedb.view.base.BaseFragment
import com.hyvu.themoviedb.viewmodel.TikMovieViewModel
import com.hyvu.themoviedb.viewmodel.factory.MainViewModelFactory
import javax.inject.Inject

class TikMovieFragment : BaseFragment() {

    @Inject
    lateinit var providerFactory: MainViewModelFactory
    private lateinit var mBinding: FragmentTikmovieBinding
    private val mViewModel by lazy {
        ViewModelProvider(this, providerFactory).get(TikMovieViewModel::class.java)
    }
    private var tikMoviePagingDataAdapter: TikMoviePagingDataAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentTikmovieBinding.bind(inflater.inflate(R.layout.fragment_tikmovie, container, false))
        return mBinding.root
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
            tikMoviePagingDataAdapter = mViewModel.movieGenres.value?.let { TikMoviePagingDataAdapter(context, listenerTikMovieViewPagerAdapter, it) }
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            PagerSnapHelper().attachToRecyclerView(this)
            adapter = tikMoviePagingDataAdapter
        }
    }

    override fun observerLiveData() {
        mViewModel.tikMovieDetails.observe(viewLifecycleOwner, { tikMovie ->
            tikMoviePagingDataAdapter?.submitData(lifecycle, tikMovie)
        })
    }

    private val listenerTikMovieViewPagerAdapter = object : TikMoviePagingDataAdapter.Listener {

    }
}