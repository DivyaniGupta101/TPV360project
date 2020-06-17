package com.tpv.android.ui.salesagent.home.enrollment.dynamicform.infotext


import com.tpv.android.databinding.LayoutInputTextInfoBinding
import com.tpv.android.model.network.DynamicFormResp


fun LayoutInputTextInfoBinding.setField(response: DynamicFormResp) {
    val binding = this
    binding.item = response
}

