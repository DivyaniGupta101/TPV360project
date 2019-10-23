package com.tpv.android.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.inexture.baseproject.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.simpleName

    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setTheme(R.style.AppTheme)
//        mBinding = setBindingView(R.layout.activity_main) // bind view
    }

}
