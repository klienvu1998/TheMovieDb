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
import com.hyvu.themoviedb.adapter.TikMoviePagingViewPagerAdapter
import com.hyvu.themoviedb.databinding.FragmentTikmovieBinding
import com.hyvu.themoviedb.viewmodel.TikMovieViewModel
import com.hyvu.themoviedb.viewmodel.factory.MainViewModelFactory
import javax.inject.Inject

class TikMovieFragment : Fragment() {

    @Inject
    lateinit var providerFactory: MainViewModelFactory
    private lateinit var mBinding: FragmentTikmovieBinding
    private val mViewModel by lazy {
        ViewModelProvider(this, providerFactory).get(TikMovieViewModel::class.java)
    }
    private var tikMoviePagingViewPagerAdapter: TikMoviePagingViewPagerAdapter? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as MainActivity).mainComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentTikmovieBinding.bind(inflater.inflate(R.layout.fragment_tikmovie, container, false))
        getData()
        return mBinding.root
    }

    private fun getData() {
        mViewModel.fetchLatestMovie()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.fetchTikMovie()
        initView()
        liveData()
    }

    private fun initView() {
        mBinding.rcvMovie.apply {
            tikMoviePagingViewPagerAdapter = mViewModel.movieGenres.value?.let { TikMoviePagingViewPagerAdapter(context, listenerTikMovieViewPagerAdapter, it) }
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            PagerSnapHelper().attachToRecyclerView(this)
            adapter = tikMoviePagingViewPagerAdapter
        }
    }

    private fun liveData() {
        mViewModel.tikMovieDetails.observe(viewLifecycleOwner, { tikMovie ->
            tikMoviePagingViewPagerAdapter?.submitData(lifecycle, tikMovie)
        })
    }

    private val listenerTikMovieViewPagerAdapter = object : TikMoviePagingViewPagerAdapter.Listener {

    }
}