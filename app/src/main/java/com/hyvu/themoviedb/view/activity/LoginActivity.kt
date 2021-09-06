package com.hyvu.themoviedb.view.activity

import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.hyvu.themoviedb.MyApplication
import com.hyvu.themoviedb.data.api.TheMovieDbClient
import com.hyvu.themoviedb.data.entity.AuthenticateToken
import com.hyvu.themoviedb.databinding.ActivityLoginBinding
import com.hyvu.themoviedb.di.LoginComponent
import com.hyvu.themoviedb.user.UserManager
import com.hyvu.themoviedb.view.base.BaseActivity
import com.hyvu.themoviedb.viewmodel.LoginViewModel
import com.hyvu.themoviedb.viewmodel.factory.MainViewModelFactory
import javax.inject.Inject

class LoginActivity : BaseActivity() {

    @Inject
    lateinit var providerFactory: MainViewModelFactory

    private val userManager: UserManager by lazy {
        (application as MyApplication).userManager
    }

    private val mViewModel by lazy {
        ViewModelProvider(this, providerFactory)[LoginViewModel::class.java]
    }

    lateinit var loginComponent: LoginComponent
    private lateinit var mBinding: ActivityLoginBinding
    private var authenticateToken: AuthenticateToken? = null

    override fun getBundle() {
        val sessionId = userManager.sessionId
        if (sessionId.isNotEmpty()) {
            startMainActivity()
        }
    }

    override fun fetchData() {

    }

    override fun inject() {
        loginComponent = (application as MyApplication).appComponent.loginComponent().create()
        loginComponent.inject(this)
    }

    override fun getLayoutId(): View {
        mBinding = ActivityLoginBinding.inflate(layoutInflater)
        return mBinding.root
    }

    override fun initView() {
        mBinding.btnLogin.setOnClickListener {
            mViewModel.fetchAuthenticateToken()
        }
        mBinding.btnLoginGuest.setOnClickListener {
            startMainActivity()
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun observerLiveData() {
        mViewModel.authenticateToken.observe(this, { authenticateToken ->
            this.authenticateToken = authenticateToken
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(TheMovieDbClient.getAuthenticateDeepLink(authenticateToken))
            startActivity(intent)
        })
        mViewModel.session.observe(this, { session ->
            if (session.success) {
                userManager.saveSessionId(session.sessionId)
                startMainActivity()
            }
        })
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.data.toString().isNotEmpty()) {
            mViewModel.fetchSession(this.authenticateToken?.requestToken!!)
        }
    }
}