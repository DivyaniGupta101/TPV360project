package com.tpv.android.ui.auth.forogtpassword


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.livinglifetechway.k4kotlin.core.androidx.toastNow
import com.livinglifetechway.k4kotlin.core.onClick
import com.tpv.android.R
import com.tpv.android.databinding.FragmentForgotPasswordBinding
import com.tpv.android.utils.validation.EmailValidator
import com.tpv.android.utils.validation.EmptyValidator
import com.tpv.android.utils.validation.TextInputValidationErrorHandler
import com.tpv.android.utils.validation.Validator

/**
 * A simple [Fragment] subclass.
 */
class ForgotPasswordFragment : Fragment() {

    private lateinit var mBinding: FragmentForgotPasswordBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_forgot_password, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.btnSubmit?.onClick {
            if (isValid()) {
                toastNow("Coming soon!!")
            }
            //            Navigation.findNavController(mBinding.root).navigate(R.id.action_forgotPasswordFragment_to_newPasswordFragment)
        }

        mBinding.textReturnToLogin?.onClick {
            Navigation.findNavController(mBinding.root).navigateUp()
        }
    }

    /**
     * Validate input
     */
    private fun isValid(): Boolean {
        return Validator(TextInputValidationErrorHandler()) {
            addValidate(
                    mBinding.editEmail,
                    EmptyValidator(),
                    getString(R.string.enter_email)
            )
            addValidate(
                    mBinding.editEmail,
                    EmailValidator(),
                    getString(R.string.enter_valid_email)
            )
        }.validate()
    }


}
