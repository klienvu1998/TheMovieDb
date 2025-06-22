package com.hyvu.themoviedb.view.base

import android.content.Context
import android.media.audiofx.DynamicsProcessing.Mbc
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.hyvu.themoviedb.utils.UserManager
import com.hyvu.themoviedb.view.homescreen.MainActivity

abstract class BaseFragment<T: ViewBinding>: Fragment() {

    private var _binding: T? = null
    protected val mBinding: T
        get() = _binding ?: throw IllegalStateException("Binding is not initialized")

    val isOnline by lazy { (activity as BaseActivity<*>).isOnline() }
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = getViewBinding(inflater, container)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        fetchData()
        observerLiveData()
    }

    abstract fun inject()
    abstract fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): T
    abstract fun getBundle()
    abstract fun fetchData()
    abstract fun initView()
    abstract fun observerLiveData()
}