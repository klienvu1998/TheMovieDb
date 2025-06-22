package com.hyvu.themoviedb.view.homescreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.databinding.FragmentHomeContainerBinding
import com.hyvu.themoviedb.view.base.BaseFragment
import com.hyvu.themoviedb.viewmodel.home.HomeViewModel
import com.hyvu.themoviedb.viewmodel.factory.MainViewModelFactory
import javax.inject.Inject

class HomeContainerFragment : BaseFragment<FragmentHomeContainerBinding>() {

    @Inject
    lateinit var providerFactory: MainViewModelFactory
    private val mViewModel by lazy {
        ViewModelProvider(this, providerFactory)[HomeViewModel::class.java]
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHomeContainerBinding {
        return FragmentHomeContainerBinding.inflate(inflater, container, false)
    }

    override fun inject() {
        (activity as MainActivity).mainComponent.inject(this)
    }

    override fun getBundle() {

    }

    override fun fetchData() {

    }

    override fun initView() {
        childFragmentManager.beginTransaction().replace(R.id.home_container, HomeFragment()).commit()
    }

    override fun observerLiveData() {

    }
}