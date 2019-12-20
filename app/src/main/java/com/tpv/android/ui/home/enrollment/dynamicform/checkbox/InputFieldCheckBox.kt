package com.tpv.android.ui.home.enrollment.dynamicform.checkbox


import android.content.Context
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.livinglifetechway.k4kotlin.core.orFalse
import com.livinglifetechway.k4kotlin.core.show
import com.tpv.android.R
import com.tpv.android.databinding.ItemInputCheckBoxBinding
import com.tpv.android.databinding.LayoutInputCheckBoxBinding
import com.tpv.android.model.network.DynamicFormResp
import com.tpv.android.model.network.Option
import com.tpv.android.utils.AppConstant

fun LayoutInputCheckBoxBinding.setField(response: DynamicFormResp) {
    val binding = this
    val context = binding.checkBoxContainer.context

    binding.item = response
    binding.item?.values = linkedMapOf(AppConstant.OPTIONS to response.meta?.options as Any)

    if (binding.item?.values?.isNotEmpty().orFalse()) {
        (binding.item?.values?.get(AppConstant.OPTIONS) as ArrayList<Option>).forEach {
            val bind = DataBindingUtil.inflate<ItemInputCheckBoxBinding>(LayoutInflater.from(context),
                    R.layout.item_input_check_box,
                    binding.checkBoxContainer,
                    true)
            bind.item = it
        }
    }
}

fun LayoutInputCheckBoxBinding.isValid(context: Context?): Boolean {
    val binding = this
    return if (binding.item?.meta?.options?.filter { it.selected == true }?.isNotEmpty().orFalse()) {
        true
    } else {
        val errorMessage = context?.getString(R.string.please_select) + " " + binding.item?.label
        binding.textError.text = errorMessage
        binding.textError.show()
        false
    }
}