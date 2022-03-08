package com.hyvu.themoviedb.view.base

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.hyvu.themoviedb.utils.UserManager
import com.hyvu.themoviedb.view.home.MainActivity

abstract class BaseFragment: Fragment() {

    val isOnline by lazy { (activity as BaseActivity).isOnline() }
    val userManager: UserManager by lazy {
        (context as MainActivity).userManager
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        inject()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getBundle()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        fetchData()
        observerLiveData()
    }

    abstract fun inject()
    abstract fun getBundle()
    abstract fun fetchData()
    abstract fun initView()
    abstract fun observerLiveData()
}