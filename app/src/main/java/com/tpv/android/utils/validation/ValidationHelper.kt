package com.tpv.android.utils.validation

import android.view.View
import android.view.ViewParent
import android.widget.EditText
import com.google.android.material.textfield.TextInputLayout
import java.util.regex.Pattern

/**
 * validation data object to hold widget, validator and error message
 */
data class ValidationObject(
        val editText: EditText,
        val validatorAndMessage: ArrayList<ValidatorAndMessage>
)

data class ValidatorAndMessage(
        val validate: Validate,
        val errorMessage: String? = null
)

/**
 * simple interface to check is input valid or not
 */
interface Validate {
    fun isValid(s: String): Boolean
}

/**
 * An abstract class to show and hide error message
 * @param isValidateAll is true then validates all widget, if isValidateAll is false than validate one by one.
 */
abstract class ValidationErrorHandler(val isValidateAll: Boolean = true) {

    /**
     * call when the input is invalid
     */
    abstract fun show(editText: EditText, errorMessage: String?)

    /**
     * call when the input is valid
     */
    open fun hide(editText: EditText) {}
}

/**
 * Simple TextInputValidationErrorHandler to handle error message using TextInputLayout
 */
class TextInputValidationErrorHandler : ValidationErrorHandler(true) {
    override fun show(editText: EditText, errorMessage: String?) {
        val parent = getInputTextLayout(editText)
        if (parent != null && parent is TextInputLayout) {
            parent.isErrorEnabled = true
            parent.isHintEnabled = false
            parent.error = errorMessage
        } else {
            editText.error = errorMessage
        }
    }

    override fun hide(editText: EditText) {
        val parent = getInputTextLayout(editText)
        if (parent != null && parent is TextInputLayout) {
            parent.error = null
        } else {
            editText.error = null
        }
    }

    /**
     * A method to get parent TextInputLayout
     * @param editText a editText view
     */
    private fun getInputTextLayout(editText: EditText): ViewParent? {
        var parent = editText.parent
        loop@ while (parent is View) {
            if (parent is TextInputLayout) {
                break@loop
            }
            parent = parent.getParent()
        }
        return parent
    }
}


/**
 * Simple EmptyValidator to validate input isNotEmpty or empty
 */
class EmptyValidator : Validate {
    override fun isValid(s: String): Boolean = s.isNotEmpty()
}

/**
 * Simple EmailValidator to validate input email is valid or not
 */
class EmailValidator : Validate {
    override fun isValid(s: String): Boolean = s.isEmailValid()
}

/**
 * Simple phone number validator
 */
class PhoneNumberValidator : Validate {
    override fun isValid(s: String): Boolean = s.length >= 10
}


/**
 * Simple password validator
 */
class PasswordValidator : Validate {
    override fun isValid(s: String): Boolean = s.length >= 8
}

/**
 * Checks if a string is a valid email
 * @return a boolean representing true if email is valid else false
 */
fun String.isEmailValid() =
        Pattern.compile("^(?:[\\p{L}0-9!#$%\\&'*+/=?\\^_`{|}~-]+(?:\\.[\\p{L}0-9!#$%\\&'*+/=?\\^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[\\p{L}0-9](?:[a-z0-9-]*[\\p{L}0-9])?\\.)+[\\p{L}0-9](?:[\\p{L}0-9-]*[\\p{L}0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[\\p{L}0-9-]*[\\p{L}0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])$").matcher(
                this
        ).matches()