package com.tpv.android.ui.home.enrollment.dynamicform.phone

import android.content.Context
import com.livinglifetechway.k4kotlin.core.onClick
import com.livinglifetechway.k4kotlin.core.orFalse
import com.livinglifetechway.k4kotlin.core.value
import com.tpv.android.R
import com.tpv.android.databinding.LayoutInputPhoneNumberBinding
import com.tpv.android.model.network.DynamicFormResp
import com.tpv.android.utils.validation.EmptyValidator
import com.tpv.android.utils.validation.PhoneNumberValidator
import com.tpv.android.utils.validation.TextInputValidationErrorHandler
import com.tpv.android.utils.validation.Validator


fun LayoutInputPhoneNumberBinding.setField(fields: DynamicFormResp) {
    val binding = this
    binding.item = fields

    binding.textVerify.onClick {

        if (binding.editPhoneNumber.value.isNotEmpty()) {
            generateOTPApiCall(context)
        } else {
            binding.isValid(context)
        }
    }
}


fun LayoutInputPhoneNumberBinding.isValid(context: Context): Boolean {
    val binding = this
    return if (binding.item?.validations?.required.orFalse()) {
        Validator(TextInputValidationErrorHandler()) {
            addValidate(
                    binding.editPhoneNumber,
                    EmptyValidator(),
                    binding.textTitle.text.toString() + " " + context.getString(R.string.is_required)
            )
            addValidate(
                    binding.editPhoneNumber,
                    PhoneNumberValidator(),
                    context.getString(R.string.enter_valid_phone_number)
            )

        }.validate()
    } else {
        return true
    }
}

fun generateOTPApiCall(context: Context) {
    context?.get

}

