package com.tpv.android.ui.home.enrollment.dynamicform.label

import com.tpv.android.databinding.LayoutInputLabelBinding
import com.tpv.android.model.network.DynamicFormResp


fun setField(resp: DynamicFormResp, binding: LayoutInputLabelBinding) {
    binding.item = resp

}

