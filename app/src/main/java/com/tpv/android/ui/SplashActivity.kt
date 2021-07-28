package com.tpv.android.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.livinglifetechway.k4kotlin.core.startActivity
import com.livinglifetechway.k4kotlin.databinding.setBindingView
import com.tpv.android.BuildConfig
import com.tpv.android.R
import com.tpv.android.databinding.ActivitySplashBinding
import com.tpv.android.model.network.ForceUpdateReq
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.ui.auth.AuthActivity
import java.net.Inet6Address
import java.net.NetworkInterface


class SplashActivity : AppCompatActivity() {
    lateinit var mBinding: ActivitySplashBinding
    private lateinit var mViewModel: SplashViewModel
    private var ipv4:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = setBindingView(R.layout.activity_splash)
        mBinding.lifecycleOwner = this
        mViewModel = ViewModelProviders.of(this).get(SplashViewModel::class.java)
        initialize()
        mBinding.errorHandler = AlertErrorHandler(mBinding.root, false, {
            finish()
        })

    }




    private fun initialize() {
        val liveData = mViewModel.forceUpdate(ForceUpdateReq(
                appVersion = BuildConfig.VERSION_CODE
        ))
        liveData.observe(this, Observer {
            it?.ifSuccess {
                startActivity<AuthActivity>()
                finish()

            }
        })
        mBinding.resource = liveData as LiveData<Resource<Any, APIError>>
    }






}

