package com.tpv.android.utils.validation

import android.widget.EditText
import com.tpv.android.model.network.DynamicFormResp
import com.tpv.android.model.network.DynamicSettingResponse
import com.tpv.android.ui.salesagent.home.enrollment.SetEnrollViewModel
import com.tpv.android.ui.salesagent.home.enrollment.dynamicform.DynamicFormFragment


/**
 * A class which hold the widget, validator and error message
 * add validation rules in sequence you want to perform in sequence
 *
 * @param errorHandler error handle to show error
 * @param init block to add validation rule
 */
class Validator(val errorHandler: ValidationErrorHandler? = null, init: Validator.() -> Unit) {

    private val widgetList = arrayListOf<ValidationObject>()

    /**
     * Adds a validation instance
     *
     * @param editText a widget to perform validation on
     * @param validate validate object which performs some kind of validation
     * @param errorMessage error message to be shown when validation fails
     */
    fun addValidate(editText: EditText, validate: Validate, errorMessage: String?) {
        val find = widgetList.find { it.editText == editText }
        if (find != null) {
            find.validatorAndMessage.add(ValidatorAndMessage(validate, errorMessage))
        } else {
            widgetList.add(ValidationObject(editText, arrayListOf(ValidatorAndMessage(validate, errorMessage))))
        }
    }


    init {
        init.invoke(this)
    }

    /**
     * validate form (validate all widget over added validator class)
     * @return Boolean
     */
    fun validate(): Boolean {
        var isValid = true

        for (validationObject in widgetList) {
            validatorAndMessage@ for (validatorAndMessage in validationObject.validatorAndMessage) {
                if (validatorAndMessage.validate.isValid(validationObject.editText.text.toString())) {
                    errorHandler?.hide(validationObject.editText)
                } else {
                    errorHandler?.show(validationObject.editText, validatorAndMessage.errorMessage)
                    isValid = false
                    //if isValidateAll true than continue validate
                    if (errorHandler?.isValidateAll == false) {
                        return isValid
                    }
                    break@validatorAndMessage
                }
            }
        }

        return isValid
    }
}