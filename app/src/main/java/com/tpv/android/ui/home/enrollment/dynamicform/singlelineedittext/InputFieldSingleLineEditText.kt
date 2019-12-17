package com.tpv.android.ui.home.enrollment.dynamicform.singlelineedittext

import android.content.Context
import android.text.InputType
import com.livinglifetechway.k4kotlin.core.orFalse
import com.tpv.android.R
import com.tpv.android.databinding.LayoutInputSingleLineEditTextBinding
import com.tpv.android.model.network.DynamicFormResp
import com.tpv.android.utils.enums.DynamicField
import com.tpv.android.utils.validation.EmailValidator
import com.tpv.android.utils.validation.EmptyValidator
import com.tpv.android.utils.validation.TextInputValidationErrorHandler
import com.tpv.android.utils.validation.Validator


fun setField(resp: DynamicFormResp, binding: LayoutInputSingleLineEditTextBinding) {
    binding.item = resp
    when (binding.item?.id) {
        DynamicField.EMAIL.type -> {
            binding.editText.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        }
    }
}


fun Context.isValid(binding: LayoutInputSingleLineEditTextBinding): Boolean {
    val context = this

    return if (binding.item?.validations?.required.orFalse()) {

        when (binding.item?.id) {
            DynamicField.EMAIL.type -> {
                Validator(TextInputValidationErrorHandler()) {
                    addValidate(
                            binding.editText,
                            EmptyValidator(),
                            context.getString(R.string.enter_email)

                    )
                    addValidate(
                            binding.editText,
                            EmailValidator(),
                            context.getString(R.string.enter_valid_email)
                    )
                }.validate()
            }
            else -> {
                Validator(TextInputValidationErrorHandler()) {
                    addValidate(
                            binding.editText,
                            EmptyValidator(),
                            context.getString(R.string.please_enter) + " " + binding.textTitle.text.toString()
                    )

                }.validate()
            }
        }
    } else {
        return true
    }
}

