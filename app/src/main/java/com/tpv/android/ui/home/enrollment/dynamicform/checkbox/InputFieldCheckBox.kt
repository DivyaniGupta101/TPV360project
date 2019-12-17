package com.tpv.android.ui.home.enrollment.dynamicform.checkbox


import android.content.Context
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.livinglifetechway.k4kotlin.core.orFalse
import com.livinglifetechway.k4kotlin.core.show
import com.livinglifetechway.k4kotlin.core.toastNow
import com.tpv.android.R
import com.tpv.android.databinding.LayoutInputCheckBoxBinding
import com.tpv.android.databinding.RowInputCheckBoxBinding
import com.tpv.android.model.network.DynamicFormResp

fun LayoutInputCheckBoxBinding.setField(response: DynamicFormResp) {
    val binding = this
    val context = binding.checkBoxContainer.context
    val list = response.meta?.options

    binding.item = response

    if (list?.isNotEmpty().orFalse()) {
        list?.forEach {
            val bind = DataBindingUtil.inflate<RowInputCheckBoxBinding>(LayoutInflater.from(context),
                    R.layout.row_input_check_box,
                    binding.checkBoxContainer,
                    true)
            bind.item = it
        }
    }
}

fun LayoutInputCheckBoxBinding.isValid(context: Context?): Boolean {
    val binding = this
    return if (binding.item?.meta?.options?.filter { it?.selected == true }?.isNotEmpty().orFalse()) {
        context?.toastNow("value= ${binding.item?.meta?.options}")
        true
    } else {
        val errorMessage = context?.getString(R.string.please_select) + " " + binding.item?.label
        binding.textError.text = errorMessage
        binding.textError.show()
        false
    }
}