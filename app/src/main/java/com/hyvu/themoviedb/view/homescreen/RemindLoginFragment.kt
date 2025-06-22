package com.hyvu.themoviedb.view.homescreen

import android.view.LayoutInflater
import android.view.ViewGroup
import com.hyvu.themoviedb.AppNavigator
import com.hyvu.themoviedb.databinding.FragmentRemindLoginBinding
import com.hyvu.themoviedb.view.base.BaseFragment
import com.hyvu.themoviedb.viewmodel.home.UserViewModel
import javax.inject.Inject

class RemindLoginFragment: BaseFragment<FragmentRemindLoginBinding>() {

    @Inject
    lateinit var mViewModel: UserViewModel

    override fun inject() {
        (activity as MainActivity).mainComponent.inject(this)
    }

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentRemindLoginBinding {
        return FragmentRemindLoginBinding.inflate(inflater, container, false)
    }

    override fun getBundle() {

    }

    override fun fetchData() {

    }

    override fun initView() {
        mBinding.btnSignIn.setOnClickListener {
            AppNavigator.startLoginScreen(requireActivity())
        }
    }

    override fun observerLiveData() {

    }

}