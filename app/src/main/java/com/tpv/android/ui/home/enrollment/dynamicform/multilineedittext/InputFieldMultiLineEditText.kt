package com.tpv.android.ui.home.enrollment.dynamicform.multilineedittext

import android.content.Context
import com.livinglifetechway.k4kotlin.core.orFalse
import com.tpv.android.R
import com.tpv.android.databinding.LayoutInputMultiLineEditTextBinding
import com.tpv.android.model.network.DynamicFormResp
import com.tpv.android.utils.validation.EmptyValidator
import com.tpv.android.utils.validation.TextInputValidationErrorHandler
import com.tpv.android.utils.validation.Validator


fun LayoutInputMultiLineEditTextBinding.setField(response: DynamicFormResp) {
    val binding = this
    binding.item = response
}


fun LayoutInputMultiLineEditTextBinding.isValid(context: Context?): Boolean {
    val binding = this

    return if (binding.item?.validations?.required.orFalse()) {
        Validator(TextInputValidationErrorHandler()) {
            addValidate(
                    binding.editText,
                    EmptyValidator(),
                    context?.getString(R.string.please_enter) + " " + binding.textTitle.text.toString()
            )
        }.validate()
    } else {
        return true
    }
}