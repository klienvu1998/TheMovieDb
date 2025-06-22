package com.hyvu.themoviedb.view.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.viewbinding.ViewBinding
import com.hyvu.themoviedb.MyApplication
import com.hyvu.themoviedb.R
import com.hyvu.themoviedb.utils.UserManager
import com.hyvu.themoviedb.utils.showToast

abstract class BaseActivity<T: ViewBinding>: AppCompatActivity() {

    private var _binding: T? = null
    protected val mBinding: T
        get() = _binding ?: throw IllegalStateException("Binding is not initialized")

    protected var isActive = false

    val userManager: UserManager by lazy {
        (application as MyApplication).userManager
    }

    fun isOnline(): Boolean {
        val connMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nw = connMgr.activeNetwork ?: return false
        val actNw = connMgr.getNetworkCapabilities(nw) ?: return false
        if (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) return true
        if (actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) return true
        //for other device how are able to connect with Ethernet
        if (actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) return true
        return false
    }

    private val registerDefaultNetworkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onLost(network: Network) {
            super.onLost(network)
            if (isActive) baseContext.showToast(getString(R.string.checking_your_network))
        }
    }

    private fun registerNetworkReceiver() {
        val connMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connMgr.registerDefaultNetworkCallback(registerDefaultNetworkCallback)
    }

    private fun unregisterNetworkReceiver() {
        val connMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connMgr.unregisterNetworkCallback(registerDefaultNetworkCallback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)
        _binding = getViewBinding()
        setContentView(mBinding.root)
        getBundle()
        fetchData()
        initView()
        observerLiveData()
    }

    abstract fun getViewBinding(): T

    override fun onStart() {
        super.onStart()
        registerNetworkReceiver()
    }

    override fun onResume() {
        super.onResume()
        isActive = true
    }

    override fun onPause() {
        super.onPause()
        isActive = false
    }

    abstract fun getBundle()

    override fun onStop() {
        super.onStop()
        unregisterNetworkReceiver()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    abstract fun fetchData()
    abstract fun inject()
    abstract fun initView()
    abstract fun observerLiveData()
}