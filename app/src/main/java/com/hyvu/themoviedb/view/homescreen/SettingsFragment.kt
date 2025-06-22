package com.hyvu.themoviedb.view.homescreen

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.hyvu.themoviedb.AppNavigator
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.databinding.FragmentUserSettingsBinding
import com.hyvu.themoviedb.view.loginscreen.LoginActivity
import com.hyvu.themoviedb.view.base.BaseFragment
import com.hyvu.themoviedb.viewmodel.setting.SettingViewEvent
import com.hyvu.themoviedb.viewmodel.setting.SettingViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsFragment : BaseFragment<FragmentUserSettingsBinding>() {

    @Inject lateinit var mViewModel: SettingViewModel

    override fun inject() {
        (activity as MainActivity).mainComponent.inject(this)
    }

    override fun getBundle() {

    }

    override fun fetchData() {

    }

    override fun initView() {
        mBinding.btnSignOut.isVisible = mViewModel.isLoggedIn()
        mBinding.btnSignOut.setOnClickListener {
            mViewModel.onLoggedOut()
            AppNavigator.startLoginScreen(requireActivity())
        }

        mBinding.toolBarContainer.apply {
            tvTitle.text = getString(R.string.settings)
            btnBack.setOnClickListener {
                activity?.onBackPressed()
            }
        }
        mBinding.swTheme.isChecked = (activity as MainActivity).userManager.isNightMode
        mBinding.swTheme.setOnCheckedChangeListener { _, isChecked ->
            (activity as MainActivity).userManager.saveIsNightMode(isChecked)
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
            )
        }
    }

    override fun observerLiveData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    mViewModel.viewEvent.collect {
                        when (it) {
                            is SettingViewEvent.ShowLoginScreen -> {
                                AppNavigator.startLoginScreen(requireActivity())
                            }
                        }
                    }
                }
            }
        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentUserSettingsBinding {
        return FragmentUserSettingsBinding.inflate(layoutInflater, container, false)
    }

}