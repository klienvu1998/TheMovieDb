package com.hyvu.themoviedb.view.homescreen

import android.view.LayoutInflater
import android.view.ViewGroup
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.databinding.FragmentUserBinding
import com.hyvu.themoviedb.view.base.BaseFragment
import com.hyvu.themoviedb.viewmodel.home.UserViewModel
import javax.inject.Inject

class UserFragment: BaseFragment<FragmentUserBinding>() {

    @Inject lateinit var mViewModel: UserViewModel

    override fun inject() {
        (activity as MainActivity).mainComponent.inject(this)
    }

    override fun getBundle() {

    }

    override fun fetchData() {

    }

    override fun initView() {
        if (mViewModel.isLoggedIn()) {
            childFragmentManager.beginTransaction().replace(R.id.container, UserHomeFragment()).commit()
        } else {
            childFragmentManager.beginTransaction().replace(R.id.container, RemindLoginFragment()).commit()
        }
    }

    override fun observerLiveData() {

    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentUserBinding {
        return FragmentUserBinding.inflate(inflater, container, false)
    }
}