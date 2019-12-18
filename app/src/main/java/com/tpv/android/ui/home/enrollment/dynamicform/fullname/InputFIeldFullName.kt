package com.tpv.android.ui.home.enrollment.dynamicform.fullname

import android.content.Context
import com.livinglifetechway.k4kotlin.core.orFalse
import com.tpv.android.R
import com.tpv.android.databinding.LayoutInputFullNameBinding
import com.tpv.android.model.network.DynamicFormResp
import com.tpv.android.utils.validation.EmptyValidator
import com.tpv.android.utils.validation.TextInputValidationErrorHandler
import com.tpv.android.utils.validation.Validator

fun LayoutInputFullNameBinding.setField(response: DynamicFormResp) {
    val binding = this
    binding.item = response
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