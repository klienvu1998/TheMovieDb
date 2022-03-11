package com.hyvu.themoviedb.view.loginscreen

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.hyvu.themoviedb.databinding.ActivityLoginWebViewBinding

class LoginWebViewActivity : AppCompatActivity() {

    companion object {
        const val RESULT_CODE = 10000
    }

    private val TAG = LoginWebViewActivity::class.java.simpleName
    lateinit var mBinding: ActivityLoginWebViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLoginWebViewBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        val url = intent.getStringExtra("url")

        mBinding.webViewLogin.apply {
            webChromeClient = object : WebChromeClient() {}
            settings.javaScriptEnabled = true
            webViewClient = object: WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    Log.d(TAG, "onPageStarted - $url")
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    Log.d(TAG, "onPageFinished - $url")
                }

                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    val uri = request?.url
                    Log.d("AAA", "shouldOverrideUrlLoading - $uri")
                    return shouldOverrideUrlLoading(uri.toString())
                }
                private fun shouldOverrideUrlLoading(url: String): Boolean {
                    if (url.contains("approved")) {
                        Log.d(TAG, "shouldOverrideUrlLoading - $url")
                        val uri = Uri.parse(url)
                        val requestToken = uri.getQueryParameter("request_token")
                        val approved = uri.getQueryParameter("approved").equals("true")
                        Log.d(TAG, "requestToken - $requestToken")
                        val intent = Intent()
                        intent.putExtra("request_token", requestToken)
                        intent.putExtra("approved", approved)
                        setResult(RESULT_CODE, intent)
                        finish()
                        return false
                    }
                    return false
                }
            }
            loadUrl(url ?: "")
        }
    }
}