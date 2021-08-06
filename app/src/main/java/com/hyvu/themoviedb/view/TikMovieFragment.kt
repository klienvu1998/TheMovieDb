package com.hyvu.themoviedb.view

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
import com.hyvu.themoviedb.viewmodel.factory.TikMovieViewModelFactory

class TikMovieFragment : Fragment() {

    private lateinit var mBinding: FragmentTikmovieBinding
    private val mViewModel by lazy {
        ViewModelProvider(this, TikMovieViewModelFactory()).get(TikMovieViewModel::class.java)
    }
    private var tikMoviePagingViewPagerAdapter: TikMoviePagingViewPagerAdapter? = null

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
            tikMoviePagingViewPagerAdapter = TikMoviePagingViewPagerAdapter(context, listenerTikMovieViewPagerAdapter)
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