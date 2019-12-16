package com.tpv.android.ui.home.enrollment.dynamicform.label

import com.tpv.android.databinding.LayoutInputHeadingBinding
import com.tpv.android.databinding.LayoutInputLabelBinding
import com.tpv.android.model.network.DynamicFormResp


fun LayoutInputLabelBinding.setField(fields: DynamicFormResp) {
    val binding = this
    binding.item = fields

}

