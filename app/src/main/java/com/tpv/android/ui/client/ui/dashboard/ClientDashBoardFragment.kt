package com.tpv.android.ui.client.ui.dashboard

import android.graphics.Bitmap
import android.os.Bundle
import android.view.*
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.livinglifetechway.k4kotlin.core.hide
import com.livinglifetechway.k4kotlin.core.show
import com.tpv.android.R
import com.tpv.android.databinding.FragmentClientDashBoardBinding
import com.tpv.android.helper.Pref
import com.tpv.android.utils.enums.ClientMenuItem
import com.tpv.android.utils.setItemSelection
import com.tpv.android.utils.setupToolbar


class ClientDashBoardFragment : Fragment() {
    private lateinit var mNavController: NavController
    lateinit var mBinding: FragmentClientDashBoardBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_client_dash_board, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mNavController = Navigation.findNavController(mBinding.root)
        initialize()
    }

    private fun initialize() {
        setupToolbar(mBinding.toolbar, getString(R.string.dashboard), showMenuIcon = true)

        mBinding.webView.webViewClient = MyWebViewClient(mBinding)
        mBinding.webView.settings.javaScriptEnabled = true
        val token = "Bearer ${Pref.token}"
        mBinding.webView.loadUrl(Pref.user?.dashBoardURL,
                mapOf("Authorization" to token))

        mBinding.webView.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action === MotionEvent.ACTION_UP && mBinding.webView.canGoBack()) {
                mBinding.webView.goBack()
                return@OnKeyListener true
            }
            false
        })
    }

    class MyWebViewClient internal constructor(binding: FragmentClientDashBoardBinding) : WebViewClient() {
        val mBinding = binding

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            mBinding.progressBarView.show()
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            mBinding.progressBarView.hide()
        }

        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            view.loadUrl(request.url.toString(), mapOf("Authorization" to "Bearer ${Pref.token}"))
            return true
        }

        override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
            view.loadUrl(url.toString(), mapOf("Authorization" to "Bearer ${Pref.token}"))
            return true
        }
    }

    override fun onResume() {
        super.onResume()
        setItemSelection(ClientMenuItem.DASHBOARD.value)
    }
}