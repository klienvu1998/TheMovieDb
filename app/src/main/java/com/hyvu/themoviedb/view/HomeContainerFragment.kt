package com.hyvu.themoviedb.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.databinding.FragmentHomeContainerBinding
import com.hyvu.themoviedb.view.activity.MainActivity
import com.hyvu.themoviedb.view.base.BaseFragment
import com.hyvu.themoviedb.viewmodel.HomeViewModel
import com.hyvu.themoviedb.viewmodel.factory.MainViewModelFactory
import javax.inject.Inject

class HomeContainerFragment : BaseFragment() {

    @Inject
    lateinit var providerFactory: MainViewModelFactory
    private val mViewModel by lazy {
        ViewModelProvider(this, providerFactory)[HomeViewModel::class.java]
    }

    private lateinit var mBinding: FragmentHomeContainerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.fragment_home_container, container, false)
        mBinding = FragmentHomeContainerBinding.bind(v)
        return mBinding.root
    }

    override fun inject() {
        (activity as MainActivity).mainComponent.inject(this)
    }

    override fun getBundle() {

    }

    override fun fetchData() {

    }

    override fun initView() {
        childFragmentManager.beginTransaction().replace(R.id.home_container, MoviesHomeFragment()).commit()
    }

    override fun observerLiveData() {

    }
}