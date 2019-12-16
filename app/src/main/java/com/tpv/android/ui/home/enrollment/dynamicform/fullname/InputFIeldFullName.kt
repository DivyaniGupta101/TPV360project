package com.tpv.android.ui.home.enrollment.dynamicform.fullname

import android.content.Context
import com.livinglifetechway.k4kotlin.core.orFalse
import com.tpv.android.R
import com.tpv.android.databinding.LayoutInputFullNameBinding
import com.tpv.android.model.network.DynamicFormResp
import com.tpv.android.utils.validation.EmptyValidator
import com.tpv.android.utils.validation.TextInputValidationErrorHandler
import com.tpv.android.utils.validation.Validator

fun LayoutInputFullNameBinding.setField(fields: DynamicFormResp) {
    val binding = this
    binding.item = fields

}


fun LayoutInputFullNameBinding.isValid(context: Context): Boolean {
    val binding = this
    return if (this.item?.validations?.required.orFalse()) {
        return Validator(TextInputValidationErrorHandler()) {
            addValidate(
                    binding.editFirstName,
                    EmptyValidator(),
                    binding.textFirstName.text.toString() + " " + context.getString(R.string.is_required)
            )
            addValidate(
                    binding.editMiddleName,
                    EmptyValidator(),
                    binding.textMiddleName.text.toString() + " " + context.getString(R.string.is_required)
            )
            addValidate(
                    binding.editLastName,
                    EmptyValidator(),
                    binding.textLastName.text.toString() + " " + context.getString(R.string.is_required)
            )
        }.validate()
    } else {
        true
    }


}