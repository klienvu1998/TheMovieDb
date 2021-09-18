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
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.hyvu.themoviedb.MyApplication
import com.hyvu.themoviedb.R

abstract class BaseActivity: AppCompatActivity() {
    private lateinit var receiver: NetworkReceiver

    fun isOnline(): Boolean {
        val connMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            val nw = connMgr.activeNetwork ?: return false
            val actNw = connMgr.getNetworkCapabilities(nw) ?: return false
            if (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) return true
            if (actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) return true
            //for other device how are able to connect with Ethernet
            if (actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) return true
        } else {
            @Suppress("DEPRECATION")
            return connMgr.activeNetworkInfo?.isConnected ?: false
        }
        return false
    }

    private val registerDefaultNetworkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            showToast(getString(R.string.checking_your_network))
        }
    }

    private fun registerNetworkReceiver() {
        val connMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            connMgr.registerDefaultNetworkCallback(registerDefaultNetworkCallback)
        } else {
            @Suppress("DEPRECATION")
            registerReceiver(receiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        }
    }

    private fun unregisterNetworkReceiver() {
        val connMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            connMgr.unregisterNetworkCallback(registerDefaultNetworkCallback)
        } else {
            @Suppress("DEPRECATION")
            unregisterReceiver(receiver)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if ((application as MyApplication).userManager.isNightMode) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        inject()
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        getBundle()
        fetchData()
        initView()
        observerLiveData()
    }

    override fun onStart() {
        super.onStart()
        registerNetworkReceiver()
    }

    abstract fun getBundle()

    override fun onStop() {
        super.onStop()
        unregisterNetworkReceiver()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    inner class NetworkReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            @Suppress("DEPRECATION")
            if (intent?.action == ConnectivityManager.CONNECTIVITY_ACTION) {
                if (isOnline()) {

                } else {
                    showToast(getString(R.string.checking_your_network))
                }
            }
        }
    }

    fun showToast(msg: String) {
        Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
    }

    abstract fun fetchData()
    abstract fun inject()
    abstract fun getLayoutId(): View
    abstract fun initView()
    abstract fun observerLiveData()
}