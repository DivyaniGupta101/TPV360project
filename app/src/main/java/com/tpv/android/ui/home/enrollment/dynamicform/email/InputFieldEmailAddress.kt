package com.tpv.android.ui.home.enrollment.dynamicform.email

import android.content.Context
import com.livinglifetechway.k4kotlin.core.orFalse
import com.tpv.android.R
import com.tpv.android.databinding.LayoutInputEmailAddressBinding
import com.tpv.android.model.network.DynamicFormResp
import com.tpv.android.utils.validation.EmailValidator
import com.tpv.android.utils.validation.EmptyValidator
import com.tpv.android.utils.validation.TextInputValidationErrorHandler
import com.tpv.android.utils.validation.Validator


fun LayoutInputEmailAddressBinding.setField(response: DynamicFormResp) {
    val binding = this
    binding.item = response
}


fun LayoutInputEmailAddressBinding.isValid(context: Context?): Boolean {
    val binding = this

    return if (binding.item?.validations?.required.orFalse()) {
        Validator(TextInputValidationErrorHandler()) {
            addValidate(
                    binding.editText,
                    EmptyValidator(),
                    context?.getString(R.string.enter_email)

            )
            addValidate(
                    binding.editText,
                    EmailValidator(),
                    context?.getString(R.string.enter_valid_email)
            )
        }.validate()
    } else {
        return true
    }
}

