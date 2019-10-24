package com.tpv.android.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.livinglifetechway.k4kotlin.databinding.setBindingView
import com.tpv.android.R
import com.tpv.android.databinding.ActivityHomeBindingImpl

class HomeActivity : AppCompatActivity() {

    lateinit var mBinding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = setBindingView(R.layout.activity_home)
    }

}
