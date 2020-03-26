package com.tpv.android.ui.home.webview

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.tpv.android.R
import com.tpv.android.databinding.FragmentWebviewBinding

/**
 * A simple [Fragment] subclass.
 */
class WebviewFragment : Fragment() {
    lateinit var mBinding: FragmentWebviewBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_webview, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initalize()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initalize() {
        mBinding.webView.loadUrl("file:///asset/test.html")
        mBinding.webView.settings.javaScriptEnabled = true
    }

}
