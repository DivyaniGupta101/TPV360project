package com.tpv.android.ui.home.enrollment.dynamicform.phone

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.livinglifetechway.k4kotlin.core.*
import com.livinglifetechway.k4kotlin.core.androidx.color
import com.tpv.android.R
import com.tpv.android.databinding.DialogOtpBinding
import com.tpv.android.databinding.FragmentDynamicFormBinding
import com.tpv.android.databinding.LayoutInputPhoneNumberBinding
import com.tpv.android.model.internal.DialogText
import com.tpv.android.model.network.DynamicFormResp
import com.tpv.android.model.network.OTPReq
import com.tpv.android.model.network.VerifyOTPReq
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.ui.home.HomeActivity
import com.tpv.android.ui.home.enrollment.SetEnrollViewModel
import com.tpv.android.utils.validation.EmptyValidator
import com.tpv.android.utils.validation.PhoneNumberValidator
import com.tpv.android.utils.validation.TextInputValidationErrorHandler
import com.tpv.android.utils.validation.Validator

var verifiedNumber: String = ""
var countryCodeList = arrayListOf("+1")


fun setField(resp: DynamicFormResp, bindingInputPhone: LayoutInputPhoneNumberBinding,
             viewModel: SetEnrollViewModel,
             bindingDynamicForm: FragmentDynamicFormBinding) {

    val context = bindingDynamicForm.btnNext.context
    bindingInputPhone.item = resp

    bindingInputPhone.spinnerCountryCode.setItems(countryCodeList)

    bindingInputPhone.textVerify.onClick {
        if (bindingInputPhone.editPhoneNumber.value.isNotEmpty()) {
            context.generateOTPApiCall(bindingInputPhone, bindingDynamicForm, viewModel)
        } else {
            context.isValid(bindingInputPhone)
        }
    }


    bindingInputPhone.editPhoneNumber.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s.toString().equals(verifiedNumber)) {
                context.handleVerifiedText(bindingInputPhone, false)
            } else {
                context.handleVerifiedText(bindingInputPhone, true)
            }
        }
    })

}


fun Context.isValid(bindingInputPhone: LayoutInputPhoneNumberBinding): Boolean {

    val context = this

    return if (bindingInputPhone.item?.validations?.required.orFalse()) {
        Validator(TextInputValidationErrorHandler()) {
            addValidate(
                    bindingInputPhone.editPhoneNumber,
                    EmptyValidator(),
                    context.getString(R.string.enter_phone_number)

            )
            addValidate(
                    bindingInputPhone.editPhoneNumber,
                    PhoneNumberValidator(),
                    context.getString(R.string.enter_valid_phone_number)
            )

        }.validate()
    } else {
        return true
    }
}

private fun Context.generateOTPApiCall(bindingInputPhone: LayoutInputPhoneNumberBinding,
                                       bindingDynamicForm: FragmentDynamicFormBinding,
                                       viewModel: SetEnrollViewModel) {
    val context = this
    val liveData = viewModel.generateOTP(OTPReq(phonenumber = bindingInputPhone.editPhoneNumber.value))
    bindingDynamicForm.lifecycleOwner?.let {
        liveData.observe(it, Observer {
            it.ifSuccess {
                context.showOTPDialog(bindingInputPhone, bindingDynamicForm, viewModel)
            }
        })
    }

    bindingDynamicForm.resource = liveData as LiveData<Resource<Any, APIError>>


}

private fun Context.showOTPDialog(bindingInputPhone: LayoutInputPhoneNumberBinding
                                  , bindingDynamicForm: FragmentDynamicFormBinding,
                                  viewModel: SetEnrollViewModel) {
    val context = this

    val binding = DataBindingUtil.inflate<DialogOtpBinding>(LayoutInflater.from(this),
            R.layout.dialog_otp, null, false)

    binding.lifecycleOwner = bindingDynamicForm.lifecycleOwner
    binding.errorHandler = AlertErrorHandler(binding.root)

    binding.item = DialogText(getString(R.string.enter_otp),
            getString(R.string.resend_otp), getString(R.string.submit), getString(R.string.cancel))

    val dialog = AlertDialog.Builder(context)
            .setView(binding.root).show()
    dialog?.setCanceledOnTouchOutside(false)
    dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    binding.pinView.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            binding.btnSubmit.isEnabled = (start == 5 && count == 1)
        }
    })

    binding?.btnSubmit?.onClick {
        context.verifyOTPApiCall(bindingInputPhone, dialog,
                binding,
                viewModel)
    }

    binding.btnCancel?.onClick {
        dialog.dismiss()
    }

    binding.textResendOTP?.onClick {
        dialog.dismiss()
        context.generateOTPApiCall(bindingInputPhone, bindingDynamicForm, viewModel)
    }
}

private fun Context.verifyOTPApiCall(bindingInputPhone: LayoutInputPhoneNumberBinding,
                                     dialog: AlertDialog,
                                     bindingOtpDialog: DialogOtpBinding,
                                     viewModel: SetEnrollViewModel) {

    val context = this
    val liveData = viewModel.verifyOTP(
            VerifyOTPReq(otp = bindingOtpDialog.pinView.value,
                    phonenumber = bindingInputPhone.editPhoneNumber.value))

    bindingOtpDialog.lifecycleOwner?.let {
        liveData.observe(it, Observer {

            it.ifSuccess {

                verifiedNumber = bindingInputPhone.editPhoneNumber.value
                dialog.dismiss()

                context.handleVerifiedText(bindingInputPhone, false)

                if (context is HomeActivity) {
                    context.hideKeyboard()
                }
            }
        })
    }

    bindingOtpDialog.resource = liveData as LiveData<Resource<Any, APIError>>
}

private fun Context.handleVerifiedText(bindingInputPhone: LayoutInputPhoneNumberBinding, isEditable: Boolean) {
    val context = this

    if (isEditable) {
        bindingInputPhone.textVerify.isEnabled = true
        bindingInputPhone.textVerify.setText(R.string.verify)
        bindingInputPhone.textVerify.setTextColor(context.color(R.color.colorTertiaryText).orZero())
    } else {
        bindingInputPhone.textVerify.isEnabled = false
        bindingInputPhone.textVerify.setText(R.string.verified)
        bindingInputPhone.textVerify.setTextColor(context.color(R.color.colorVerifiedText).orZero())
    }
}