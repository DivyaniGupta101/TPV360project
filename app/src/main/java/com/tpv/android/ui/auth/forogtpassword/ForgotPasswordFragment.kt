package com.tpv.android.ui.auth.forogtpassword


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.livinglifetechway.k4kotlin.core.androidx.hideKeyboard
import com.livinglifetechway.k4kotlin.core.onClick
import com.livinglifetechway.k4kotlin.core.value
import com.tpv.android.R
import com.tpv.android.databinding.FragmentForgotPasswordBinding
import com.tpv.android.model.network.ForgotPasswordReq
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.utils.infoDialog
import com.tpv.android.utils.navigateSafe
import com.tpv.android.utils.validation.EmailValidator
import com.tpv.android.utils.validation.EmptyValidator
import com.tpv.android.utils.validation.TextInputValidationErrorHandler
import com.tpv.android.utils.validation.Validator


class ForgotPasswordFragment : Fragment() {

    private lateinit var mBinding: FragmentForgotPasswordBinding
    private lateinit var mViewModel: ForgotPasswordViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_forgot_password, container, false)
        mBinding.lifecycleOwner = this
        mViewModel = ViewModelProviders.of(this).get(ForgotPasswordViewModel::class.java)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    private fun initialize() {

        mBinding.errorHandler = AlertErrorHandler(mBinding.root)
        mBinding.btnSubmit?.onClick {
            if (isValid()) {
                hideKeyboard()
                forgotPasswordApiCall()
            }
        }

        mBinding.textReturnToLogin?.onClick {
            Navigation.findNavController(mBinding.root).navigateUp()
        }
    }

    private fun forgotPasswordApiCall() {
        val liveData = mViewModel.forgotPassword(ForgotPasswordReq(mBinding.editEmail.value))
        liveData.observe(this, Observer {
            it.ifSuccess {
                context?.infoDialog(subTitleText = it?.message.orEmpty(),
                        title = getString(R.string.success),
                        setOnButtonClickListener = {
                            Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_forgotPasswordFragment_to_loginFragment)
                        })
            }
        })

        mBinding.resource = liveData as LiveData<Resource<Any, APIError>>
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
