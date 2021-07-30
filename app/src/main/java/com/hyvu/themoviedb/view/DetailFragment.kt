package com.hyvu.themoviedb.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.databinding.FragmentDetailBinding
import com.hyvu.themoviedb.viewmodel.DetailViewModel
import com.hyvu.themoviedb.viewmodel.factory.DetailViewModelFactory

class DetailFragment : Fragment() {

    private lateinit var mBinding: FragmentDetailBinding
    private val mViewModel by lazy {
        ViewModelProvider(this, DetailViewModelFactory()).get(DetailViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.fragment_detail, container, false)
        mBinding = FragmentDetailBinding.bind(v)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        liveData()
    }

    private fun liveData() {
        mViewModel.movieDetails.observe(viewLifecycleOwner, { movieDetails ->
            mBinding.tvContent.text = movieDetails.overview
        })
    }

}