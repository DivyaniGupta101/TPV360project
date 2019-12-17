package com.tpv.android.ui.home.enrollment.dynamicform.heading


import com.tpv.android.databinding.LayoutInputHeadingBinding
import com.tpv.android.model.network.DynamicFormResp


fun setField(resp: DynamicFormResp, binding: LayoutInputHeadingBinding) {
    binding.item = resp
}

