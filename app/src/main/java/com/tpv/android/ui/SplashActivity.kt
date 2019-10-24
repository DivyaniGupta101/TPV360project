package com.tpv.android.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.livinglifetechway.k4kotlin.core.startActivity
import com.livinglifetechway.k4kotlin.databinding.setBindingView
import com.tpv.android.R
import com.tpv.android.ui.auth.AuthActivity
import com.tpv.android.databinding.ActivitySplashBinding


class SplashActivity : AppCompatActivity() {
    lateinit var mBinding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = setBindingView(R.layout.activity_splash)
        startActivity<AuthActivity>()
        finish()

    }
}

