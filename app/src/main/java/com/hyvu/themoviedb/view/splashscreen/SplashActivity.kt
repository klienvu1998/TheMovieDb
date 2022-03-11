package com.hyvu.themoviedb.view.splashscreen

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.View
import com.hyvu.themoviedb.databinding.ActivitySplashBinding
import com.hyvu.themoviedb.view.base.BaseActivity
import com.hyvu.themoviedb.view.homescreen.MainActivity
import com.hyvu.themoviedb.view.loginscreen.LoginActivity

class SplashActivity : BaseActivity() {

    private lateinit var mBinding: ActivitySplashBinding

    override fun getBundle() {
    }

    override fun fetchData() {
    }

    override fun inject() {
    }

    override fun getLayoutId(): View {
        mBinding = ActivitySplashBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun initView() {
        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            finish()
            if (userManager.sessionId.isEmpty()) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }, 2000)
    }

    override fun observerLiveData() {
    }
}