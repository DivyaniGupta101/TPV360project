package com.tpv.android.ui.salesagent.home.enrollment.dynamicform.heading


import com.tpv.android.databinding.LayoutInputHeadingBinding
import com.tpv.android.model.network.DynamicFormResp


fun LayoutInputHeadingBinding.setField(response: DynamicFormResp) {
    val binding = this
    binding.item = response
}

