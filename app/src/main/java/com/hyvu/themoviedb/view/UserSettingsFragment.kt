package com.hyvu.themoviedb.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatDelegate
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.databinding.FragmentUserSettingsBinding
import com.hyvu.themoviedb.view.activity.LoginActivity
import com.hyvu.themoviedb.view.activity.MainActivity
import com.hyvu.themoviedb.view.base.BaseFragment

class UserSettingsFragment : BaseFragment() {

    private lateinit var mBinding: FragmentUserSettingsBinding

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
            if (isChecked) {
                (activity as MainActivity).userManager.saveIsNightMode(true)
            } else {
                (activity as MainActivity).userManager.saveIsNightMode(false)
            }
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }

    private fun startLoginScreen() {
        val intent = Intent(context, LoginActivity::class.java)
        startActivity(intent)
        (activity as MainActivity).finish()
    }

    override fun observerLiveData() {
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.fragment_user_settings, container, false)
        mBinding = FragmentUserSettingsBinding.bind(v)
        return mBinding.root
    }

}