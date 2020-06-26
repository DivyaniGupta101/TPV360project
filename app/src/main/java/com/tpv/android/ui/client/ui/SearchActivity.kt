package com.tpv.android.ui.client.ui

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import com.livinglifetechway.k4kotlin.core.hideKeyboard
import com.livinglifetechway.k4kotlin.core.onClick
import com.livinglifetechway.k4kotlin.core.value
import com.livinglifetechway.k4kotlin.databinding.setBindingView
import com.ravikoradiya.liveadapter.LiveAdapter
import com.tpv.android.BR
import com.tpv.android.R
import com.tpv.android.databinding.ActivitySearchBinding
import com.tpv.android.databinding.ItemSearchBinding
import com.tpv.android.helper.Pref
import com.tpv.android.ui.client.ui.reports.reportslisting.ClientReportsListingFragment


class SearchActivity : AppCompatActivity() {

    lateinit var mBinding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = setBindingView(R.layout.activity_search)

        var value = intent.getStringExtra("searchText")

        if (!value.isNullOrBlank()){
            mBinding.editSearch.setText(value)
        }

        mBinding.editSearch.requestFocus()

        LiveAdapter(Pref.searchText.toList(), BR.item)
                .map<String, ItemSearchBinding>(R.layout.item_search) {
                    onClick {
                        passValueToFragment(it.binding.item.orEmpty())
                    }
                }
                .into(mBinding.rvSearch)

        mBinding.imageToolbarBack.onClick {
            finish()
        }

        mBinding.editSearch.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if(!mBinding.editSearch.value.isNullOrBlank()) {
                    Pref.searchText.add(mBinding.editSearch.value)
                }
                passValueToFragment(mBinding.editSearch.value)
                return@setOnEditorActionListener true
            }
            false
        }

    }

    private fun passValueToFragment(value: String) {
        hideKeyboard()
        val intent = Intent()
        intent.putExtra(ClientReportsListingFragment.EXTRA_KEY_SEARCH, value)
        setResult(ClientReportsListingFragment.RESULT_CODE, intent) // You can also send result without any data using setResult(int resultCode)
        finish()
    }
}