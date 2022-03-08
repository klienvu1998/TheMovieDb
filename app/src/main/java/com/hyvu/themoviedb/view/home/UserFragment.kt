package com.hyvu.themoviedb.view.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.databinding.FragmentUserBinding
import com.hyvu.themoviedb.view.base.BaseFragment
import com.hyvu.themoviedb.viewmodel.home.UserViewModel
import com.hyvu.themoviedb.viewmodel.factory.MainViewModelFactory
import javax.inject.Inject

class UserFragment : BaseFragment() {

    @Inject
    lateinit var providerFactory: MainViewModelFactory
    private val mViewModel by lazy {
        ViewModelProvider(this, providerFactory)[UserViewModel::class.java]
    }
    private lateinit var mBinding: FragmentUserBinding

    override fun inject() {
        (activity as MainActivity).mainComponent.inject(this)
    }

    override fun getBundle() {

    }

    override fun fetchData() {

    }

    override fun initView() {
        childFragmentManager.beginTransaction().replace(R.id.container, UserHomeFragment()).commit()
    }

    override fun observerLiveData() {

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.fragment_user, container, false)
        mBinding = FragmentUserBinding.bind(v)
        return mBinding.root
    }
}