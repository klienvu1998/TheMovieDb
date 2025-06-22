package com.hyvu.themoviedb.view.splashscreen

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.hyvu.themoviedb.AppNavigator
import com.hyvu.themoviedb.MyApplication
import com.hyvu.themoviedb.databinding.ActivitySplashBinding
import com.hyvu.themoviedb.di.LoginComponent
import com.hyvu.themoviedb.di.SplashScreenComponent
import com.hyvu.themoviedb.utils.showToast
import com.hyvu.themoviedb.view.base.BaseActivity
import com.hyvu.themoviedb.view.homescreen.MainActivity
import com.hyvu.themoviedb.view.loginscreen.LoginActivity
import com.hyvu.themoviedb.viewmodel.login.LoginViewModel
import com.hyvu.themoviedb.viewmodel.splashscreen.SplashScreenViewEvent
import com.hyvu.themoviedb.viewmodel.splashscreen.SplashScreenViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class SplashActivity : BaseActivity<ActivitySplashBinding>() {

    private lateinit var splashComponent: SplashScreenComponent
    @Inject
    lateinit var mViewModel: SplashScreenViewModel

    override fun getBundle() {
    }

    override fun fetchData() {
        mViewModel.checkValidSession()
    }

    override fun inject() {
        splashComponent = (application as MyApplication).appComponent.splashComponent().create()
        splashComponent.inject(this)
    }

    override fun getViewBinding(): ActivitySplashBinding {
        return ActivitySplashBinding.inflate(layoutInflater)
    }

    override fun initView() {
    }

    override fun observerLiveData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    mViewModel.viewEvent.collect {
                        when (it) {
                            is SplashScreenViewEvent.ShowHomeScreen -> {
                                finish()
                                AppNavigator.startHomeScreen(this@SplashActivity)
                            }
                            is SplashScreenViewEvent.ShowToast -> {
                                showToast(getString(it.stringId))
                            }
                            is SplashScreenViewEvent.ShowLoginScreen -> {
                                finish()
                                AppNavigator.startLoginScreen(this@SplashActivity)
                            }
                        }
                    }
                }
            }
        }
    }
}