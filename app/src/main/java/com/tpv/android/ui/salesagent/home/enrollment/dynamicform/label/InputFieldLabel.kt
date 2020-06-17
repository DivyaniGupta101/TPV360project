package com.tpv.android.ui.salesagent.home.enrollment.dynamicform.label

import com.tpv.android.databinding.LayoutInputLabelBinding
import com.tpv.android.model.network.DynamicFormResp


fun LayoutInputLabelBinding.setField(response: DynamicFormResp) {
    val binding = this
    binding.item = response

}

