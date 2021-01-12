package com.tpv.android.ui.salesagent.home.enrollment.dynamicform.fullname

import android.content.Context
import com.livinglifetechway.k4kotlin.core.onClick
import com.livinglifetechway.k4kotlin.core.orFalse
import com.livinglifetechway.k4kotlin.core.value
import com.tpv.android.R
import com.tpv.android.databinding.LayoutInputFullNameBinding
import com.tpv.android.model.network.DynamicFormResp
import com.tpv.android.utils.BindingAdapter.setAllCaps
import com.tpv.android.utils.copyTextDialog
import com.tpv.android.utils.validation.EmptyValidator
import com.tpv.android.utils.validation.TextInputValidationErrorHandler
import com.tpv.android.utils.validation.Validator

fun LayoutInputFullNameBinding.setField(response: DynamicFormResp, listOfCopyTextForAddress: ArrayList<DynamicFormResp>) {
    val binding = this
    binding.item = response


//    binding.item?.meta?.let { setAllCaps(binding.editFirstName, it) }
//    binding.item?.meta?.isAllCaps?.let { setAllCaps(binding.editLastName, it) }
//    binding.item?.meta?.isAllCaps?.let { setAllCaps(binding.editMiddleName, it) }

    binding.editFirstName.setOnFocusChangeListener { v, hasFocus ->
        if (!hasFocus) {
            binding.editFirstName.value = binding.editFirstName.value.capitalize()
        }
    }

    binding.editMiddleName.setOnFocusChangeListener { v, hasFocus ->
        if (!hasFocus) {
            binding.editMiddleName.value = binding.editMiddleName.value.capitalize()
        }
    }

    binding.editLastName.setOnFocusChangeListener { v, hasFocus ->
        if (!hasFocus) {
            binding.editLastName.value = binding.editLastName.value.capitalize()
        }
    }

    binding.textCopyFrom.onClick {
        context.copyTextDialog(
                list = listOfCopyTextForAddress,
                response = response,
                updateView =
                {
                    binding.invalidateAll()

                }
        )
    }
}


fun LayoutInputFullNameBinding.isValid(context: Context?): Boolean {
    val binding = this

    return if (binding.item?.validations?.required.orFalse()) {
        return Validator(TextInputValidationErrorHandler()) {
            addValidate(
                    binding.editFirstName,
                    EmptyValidator(),
                    context?.getString(R.string.enter_first_name)
            )
            addValidate(
                    binding.editLastName,
                    EmptyValidator(),
                    context?.getString(R.string.enter_last_name)
            )
        }.validate()
    } else {
        true
    }
}