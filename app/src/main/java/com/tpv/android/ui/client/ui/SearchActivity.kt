package com.tpv.android.ui.client.ui

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import com.livinglifetechway.k4kotlin.core.hideKeyboard
import com.livinglifetechway.k4kotlin.core.onClick
import com.livinglifetechway.k4kotlin.core.value
import com.livinglifetechway.k4kotlin.databinding.setBindingView
import com.tpv.android.R
import com.tpv.android.databinding.ActivitySearchBinding
import com.tpv.android.ui.client.ui.reports.reportslisting.ClientReportsListingFragment


class SearchActivity : AppCompatActivity() {

    lateinit var mBinding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = setBindingView(R.layout.activity_search)

        mBinding.imageToolbarBack.onClick {
            finish()
        }

        mBinding.editSearch.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard()
                val intent = Intent()
                intent.putExtra(ClientReportsListingFragment.EXTRA_KEY_SEARCH, mBinding.editSearch.value)
                setResult(ClientReportsListingFragment.RESULT_CODE, intent) // You can also send result without any data using setResult(int resultCode)
                finish()
                return@setOnEditorActionListener true
            }
            false
        }

    }
}