package com.tpv.android.ui.home.support

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.livinglifetechway.k4kotlin.core.androidx.hideKeyboard
import com.livinglifetechway.k4kotlin.core.onClick
import com.tpv.android.R
import com.tpv.android.databinding.FragmentSupportBinding
import com.tpv.android.utils.setupToolbar
import com.tpv.android.utils.validation.EmptyValidator
import com.tpv.android.utils.validation.TextInputValidationErrorHandler
import com.tpv.android.utils.validation.Validator

/**
 * A simple [Fragment] subclass.
 */
class SupportFragment : Fragment() {
    lateinit var mBinding: FragmentSupportBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_support, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initalize()
    }

    private fun initalize() {
        setupToolbar(mBinding.toolbar, getString(R.string.support), showBackIcon = true)
        mBinding.btnSubmit.onClick {
            if (isValid()) {
                hideKeyboard()
                Navigation.findNavController(mBinding.root).navigate(R.id.action_supportFragment_to_supportSuccessFragment)
            }
        }
    }

    fun isValid(): Boolean {

        return Validator(TextInputValidationErrorHandler()) {
            addValidate(
                    mBinding.editSubject,
                    EmptyValidator(),
                    context?.getString(R.string.enter_subject)
            )
            addValidate(
                    mBinding.editDescription,
                    EmptyValidator(),
                    context?.getString(R.string.enter_description)
            )
        }.validate()
    }
}
