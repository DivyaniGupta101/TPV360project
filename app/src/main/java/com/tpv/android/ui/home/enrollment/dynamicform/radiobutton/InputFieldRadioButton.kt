package com.tpv.android.ui.home.enrollment.dynamicform.radiobutton

import android.content.Context
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.livinglifetechway.k4kotlin.core.orFalse
import com.livinglifetechway.k4kotlin.core.show
import com.tpv.android.R
import com.tpv.android.databinding.LayoutInputRadioButtonBinding
import com.tpv.android.databinding.RowInputRadioButtonBinding
import com.tpv.android.model.network.DynamicFormResp

fun LayoutInputRadioButtonBinding.setField(response: DynamicFormResp) {
    val binding = this
    val context = binding.radioContainer.context
    val list = response.meta?.options

    binding.item = response

    if (list?.isNotEmpty().orFalse()) {
        list?.forEach {
            val bind = DataBindingUtil.inflate<RowInputRadioButtonBinding>(LayoutInflater.from(context),
                    R.layout.row_input_radio_button,
                    binding.radioContainer,
                    true)
            bind.item = it
        }
    }
}

fun LayoutInputRadioButtonBinding.isValid(context: Context?): Boolean {
    val binding = this
    return if (binding.item?.meta?.options?.filter { it?.selected == true }?.isNotEmpty().orFalse()) {
        true
    } else {
        val errorMessage = context?.getString(R.string.please_select) + " " + binding.item?.label
        binding.textError.text = errorMessage
        binding.textError.show()
        false
    }

}