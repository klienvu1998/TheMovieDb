package com.hyvu.themoviedb.view.homescreen

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.databinding.FragmentUserSettingsBinding
import com.hyvu.themoviedb.view.loginscreen.LoginActivity
import com.hyvu.themoviedb.view.base.BaseFragment

class UserSettingsFragment : BaseFragment<FragmentUserSettingsBinding>() {

    override fun inject() {
        (activity as MainActivity).mainComponent.inject(this)
    }

    override fun getBundle() {

    }

    override fun fetchData() {

    }

    override fun initView() {
        if ((activity as MainActivity).userManager.sessionId.isNotEmpty()) {
            mBinding.signInSuggest.visibility = View.GONE
            mBinding.loggedInContainer.visibility = View.VISIBLE
        }
        mBinding.btnUserSignOut.setOnClickListener {
            (activity as MainActivity).userManager.saveSessionId("")
            startLoginScreen()
        }
        mBinding.toolBarContainer.apply {
            tvTitle.text = getString(R.string.settings)
            btnBack.setOnClickListener {
                activity?.onBackPressed()
            }
        }
        mBinding.btnSignIn.setOnClickListener {
            startLoginScreen()
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

    private fun startLoginScreen() {
        val intent = Intent(context, LoginActivity::class.java)
        startActivity(intent)
        (activity as MainActivity).finish()
    }

    override fun observerLiveData() {
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentUserSettingsBinding {
        return FragmentUserSettingsBinding.inflate(layoutInflater, container, false)
    }

}