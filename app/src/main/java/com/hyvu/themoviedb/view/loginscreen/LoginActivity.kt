package com.hyvu.themoviedb.view.loginscreen

import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.hyvu.themoviedb.MyApplication
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.data.remote.api.TheMovieDbClient
import com.hyvu.themoviedb.databinding.ActivityLoginBinding
import com.hyvu.themoviedb.di.LoginComponent
import com.hyvu.themoviedb.utils.showToast
import com.hyvu.themoviedb.view.base.BaseActivity
import com.hyvu.themoviedb.view.homescreen.MainActivity
import com.hyvu.themoviedb.viewmodel.login.LoginViewEvent
import com.hyvu.themoviedb.viewmodel.login.LoginViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginActivity: BaseActivity<ActivityLoginBinding>() {

    @Inject lateinit var mViewModel: LoginViewModel

    private lateinit var loginComponent: LoginComponent

    override fun getBundle() {
        val sessionId = userManager.sessionId
        if (sessionId.isNotEmpty()) {
            startHomeScreen()
        }
    }

    override fun fetchData() {

    }

    override fun inject() {
        loginComponent = (application as MyApplication).appComponent.loginComponent().create()
        loginComponent.inject(this)
    }

    override fun getViewBinding(): ActivityLoginBinding {
        return ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun initView() {
        mBinding.slideImageView.apply {
            setData(listOf(R.drawable.login_bg1, R.drawable.login_bg1, R.drawable.login_bg1))
            startAnimation()
        }
        mBinding.btnLogin.setOnClickListener {
            mViewModel.createAuthenticateToken()
        }
        mBinding.btnLoginGuest.setOnClickListener {
            mViewModel.createGuestSession()
        }
    }

    private fun startHomeScreen() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private val intentResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == LoginWebViewActivity.RESULT_CODE) {
            val requestToken = it.data?.extras?.getString("request_token") ?: ""
            val approved = it.data?.extras?.getBoolean("approved") as Boolean
            if (approved) {
                mViewModel.createUserSession(requestToken)
            } else {
                showToast(getString(R.string.normal_error))
            }
        }
    }

    override fun observerLiveData() {
        mViewModel.isLoading.observe(this) {
            mBinding.loadingView.root.isVisible = it
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    mViewModel.viewEvent.collect {
                        when (it) {
                            is LoginViewEvent.ShowHomeScreen -> {
                                startHomeScreen()
                            }
                            is LoginViewEvent.ShowToast -> {
                                showToast(getString(it.stringId))
                            }
                            is LoginViewEvent.ShowWebLogin -> {
                                val intent = Intent(this@LoginActivity, LoginWebViewActivity::class.java)
                                intent.putExtra("url", TheMovieDbClient.getAuthenticateDeepLink(it.authenticateToken.requestToken))
                                intentResult.launch(intent)
                            }
                        }
                    }
                }
            }
        }
    }
}