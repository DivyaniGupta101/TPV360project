package com.tpv.android.ui.salesagent.home.enrollment.dynamicform.spinner

import com.livinglifetechway.k4kotlin.core.onItemSelected
import com.livinglifetechway.k4kotlin.core.orFalse
import com.livinglifetechway.k4kotlin.core.orZero
import com.livinglifetechway.k4kotlin.core.setItems
import com.tpv.android.databinding.LayoutInputSpinnerBinding
import com.tpv.android.model.network.DynamicFormResp
import com.tpv.android.model.network.Option
import com.tpv.android.utils.AppConstant

fun LayoutInputSpinnerBinding.setField(response: DynamicFormResp) {

    val binding = this
    binding.item = response

    binding.item?.values = linkedMapOf(AppConstant.OPTIONS to response.meta?.options as Any)
    val listOfOption = response.values[AppConstant.OPTIONS] as ArrayList<Option>
    val spinnerValueList = listOfOption.map { it.option.orEmpty() }


    binding.spinner?.setItems(spinnerValueList as ArrayList<String>?)

    //Check if listOfOption contain any true value then set that item as a selected
    if (listOfOption.filter { it.selected == true }.isNotEmpty().orFalse()) {
        val selectedItem = listOfOption.find { it.selected == true }?.option
        binding.spinner?.setSelection(spinnerValueList.indexOf(selectedItem).orZero())
    }

    binding.spinner?.onItemSelected { parent, view, position, id ->
        listOfOption.forEachIndexed { index, option ->
            option.selected = (index == position)
        }

    }

}