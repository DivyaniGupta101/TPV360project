package com.tpv.android.utils.validation

import android.widget.CheckBox
import android.widget.EditText
import com.livinglifetechway.k4kotlin.core.addTextWatcher
import com.livinglifetechway.k4kotlin.core.orFalse


/**
 * simple interface to check is input valid or not
 */
abstract class BaseValidator {
    var onUpdateListener: (() -> Unit)? = null
    abstract var isValid: Boolean
}


class EditTextEmptyValidator(editText: EditText) : BaseValidator() {
    override var isValid: Boolean = false

    init {
        editText.addTextWatcher { s, _, _, _ ->
            isValid = s?.isNotEmpty().orFalse()
            onUpdateListener?.invoke()
        }
    }

}

class CheckBoxValidator(vararg checkBoxes: CheckBox) : BaseValidator() {
    override var isValid: Boolean = false

    private val mCheckBoxes = arrayListOf<CheckBox>()

    fun add(checkBox: CheckBox) {
        mCheckBoxes.add(checkBox)

        mCheckBoxes.forEach { cb ->
            cb.setOnCheckedChangeListener { _, _ ->
                isValid = mCheckBoxes.find { it.isChecked } != null
                onUpdateListener?.invoke()
            }
        }
    }

    init {
        if (checkBoxes.isNotEmpty()) mCheckBoxes.addAll(checkBoxes)

        mCheckBoxes.forEach { checkBox ->
            checkBox.setOnCheckedChangeListener { _, _ ->
                isValid = mCheckBoxes.find { it.isChecked } != null
                onUpdateListener?.invoke()
            }
        }

    }
}