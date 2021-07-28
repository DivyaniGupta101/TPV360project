package com.tpv.android.ui.salesagent.home.enrollment.dynamicform.email

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import com.tpv.android.databinding.LayoutInputEmailAddressBinding
import com.tpv.android.model.internal.DialogText
import com.tpv.android.model.network.DynamicFormResp
import com.tpv.android.model.network.OTPEmailReq
import com.tpv.android.model.network.VerifyOTPEmailReq
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.network.resources.extensions.ifFailure
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.ui.salesagent.home.HomeActivity
import com.tpv.android.ui.salesagent.home.enrollment.SetEnrollViewModel
import com.tpv.android.ui.salesagent.home.enrollment.dynamicform.DynamicFormFragment
import com.tpv.android.utils.copyTextDialog
import com.tpv.android.utils.validation.EmailValidator
import com.tpv.android.utils.validation.EmptyValidator
import com.tpv.android.utils.validation.TextInputValidationErrorHandler
import com.tpv.android.utils.validation.Validator


fun LayoutInputEmailAddressBinding.setField(response: DynamicFormResp,
                                            viewModel: SetEnrollViewModel,
                                            bindingDynamicForm: FragmentDynamicFormBinding,
                                            list: ArrayList<DynamicFormResp>) {
    val bindingInputEmail = this
    val context = bindingInputEmail.textVerify.context

    bindingInputEmail.item = response




    if (bindingInputEmail.item?.validations?.verify.orFalse()) {
        bindingInputEmail.textVerify.show()
    } else {
        bindingInputEmail.textVerify.hide()
    }

    bindingInputEmail.textVerify.onClick {

        context.hideKeyBoard()

        //Check if phone number is valid then only able to call api generate otp
        if (bindingInputEmail.isValid(context)) {
            context.generateOTPApiCall(bindingInputEmail, bindingDynamicForm, viewModel)
        }
    }

    bindingInputEmail.textCopyFrom.onClick {
        context.copyTextDialog(
                list = list,
                response = response,
                updateView =
                {
                    bindingInputEmail.invalidateAll()

                }
        )
    }
    bindingInputEmail.editText.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            //Check if number is already verified call handleVerifiedText method
            if (s.toString().equals(viewModel.emailVerified)) {
                context.handleVerifiedText(bindingInputEmail, false)
            } else {
                context.handleVerifiedText(bindingInputEmail, true)
            }
        }
    })

}


fun LayoutInputEmailAddressBinding.isValid(context: Context?): Boolean {
    val binding = this

    return if (binding.item?.validations?.required.orFalse()) {
        Log.e("validations", binding.item?.validations?.required.orFalse().toString())
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
        Log.e("validation_else","validation_else")
        return true
    }
}

/**
 * Generate otp on this email
 * On success open otp Dialog
 */
private fun Context.generateOTPApiCall(bindingInputEmail: LayoutInputEmailAddressBinding,
                                       bindingDynamicForm: FragmentDynamicFormBinding,
                                       viewModel: SetEnrollViewModel) {
    val context = this
    val liveData = viewModel.generateEmailOTP(OTPEmailReq(emailAddress = bindingInputEmail.editText.value))
    bindingDynamicForm.lifecycleOwner?.let {
        liveData.observe(it, Observer {
            it.ifSuccess {
                context.otpDialog(bindingInputEmail, bindingDynamicForm, viewModel)
            }
        })
    }

    bindingDynamicForm.resource = liveData as LiveData<Resource<Any, APIError>>
}

/**
 * On click of submit button call verify otp api
 * On click of resend otp call generate api
 */
private fun Context.otpDialog(bindingInputEmail: LayoutInputEmailAddressBinding, bindingDynamicForm: FragmentDynamicFormBinding,
                              viewModel: SetEnrollViewModel) {
    val context = this

    val binding = DataBindingUtil.inflate<DialogOtpBinding>(LayoutInflater.from(this),
            R.layout.dialog_otp, null, false)

    binding.lifecycleOwner = bindingDynamicForm.lifecycleOwner
//    binding.errorHandler = AlertErrorHandler(binding.root)

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
            //Only submit button enable while otp number fill
            binding.btnSubmit.isEnabled = (start == 5 && count == 1)
        }
    })

    binding?.btnSubmit?.onClick {
        context.verifyOTPApiCall(bindingInputEmail, dialog,
                binding,
                viewModel)
    }

    binding.btnCancel?.onClick {
        dialog.dismiss()
    }

    binding.textResendOTP?.onClick {
        dialog.dismiss()
        context.generateOTPApiCall(bindingInputEmail, bindingDynamicForm, viewModel)
    }
}

/**
 * On success call handleVerifiedText method and dismiss otp dialog
 * Add this number in verifiedNumber
 */
private fun Context.verifyOTPApiCall(bindingInputEmail: LayoutInputEmailAddressBinding,
                                     dialog: AlertDialog,
                                     bindingOtpDialog: DialogOtpBinding,
                                     viewModel: SetEnrollViewModel) {

    val context = this
    val liveData = viewModel.verifyEmailOTP(
            VerifyOTPEmailReq(otp = bindingOtpDialog.pinView.value,
                    emailAddress = bindingInputEmail.editText.value))

    bindingOtpDialog.lifecycleOwner?.let {
        liveData.observe(it, Observer {

            it.ifSuccess {

                viewModel.emailVerified = bindingInputEmail.editText.value
                dialog.dismiss()

                context.handleVerifiedText(bindingInputEmail, false)
                context.hideKeyBoard()
            }
            it.ifFailure { throwable, errorData ->
                bindingOtpDialog.textError.show()
            }
        })
    }
    bindingOtpDialog.resource = liveData as LiveData<Resource<Any, APIError>>
}


/**
 * Check if isEditable is true then button should be enable and change text and text color
 */
private fun Context.handleVerifiedText(bindingInputEmail: LayoutInputEmailAddressBinding, isEditable: Boolean) {
    val context = this

    if (isEditable) {
        bindingInputEmail.textVerify.isEnabled = true
        bindingInputEmail.textVerify.setText(R.string.verify)
        bindingInputEmail.textVerify.setTextColor(context.color(R.color.colorTertiaryText).orZero())
    } else {
        bindingInputEmail.textVerify.isEnabled = false
        bindingInputEmail.textVerify.setText(R.string.verified)
        bindingInputEmail.textVerify.setTextColor(context.color(R.color.colorVerifiedText).orZero())
    }
}

private fun Context.hideKeyBoard() {
    val context = this
    if (context is HomeActivity) {
        context.hideKeyboard()
    }
}

