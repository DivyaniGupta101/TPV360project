package com.tpv.android.ui.auth.login


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
import com.livinglifetechway.k4kotlin.core.startActivity
import com.livinglifetechway.k4kotlin.core.value
import com.tpv.android.BuildConfig
import com.tpv.android.R
import com.tpv.android.databinding.FragmentLoginBinding
import com.tpv.android.helper.Pref
import com.tpv.android.model.network.LoginReq
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.ui.home.HomeActivity
import com.tpv.android.utils.navigateSafe
import com.tpv.android.utils.validation.EmailValidator
import com.tpv.android.utils.validation.EmptyValidator
import com.tpv.android.utils.validation.TextInputValidationErrorHandler
import com.tpv.android.utils.validation.Validator

class LoginFragment : Fragment() {
    lateinit var mBinding: FragmentLoginBinding
    private lateinit var mViewModel: LoginViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        mBinding.lifecycleOwner = this
        mViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    private fun initialize() {
        mBinding.errorHandler = AlertErrorHandler(mBinding.root)

        if (Pref.token != null) {
            context.startActivity<HomeActivity>()
            activity?.finish()
        }
        if (BuildConfig.DEBUG) {
            mBinding.editEmail.setText("mansi.d2d_agent+1@gmail.com")
            mBinding.editPassword.setText("tpv@123")

        }

        mBinding.btnStart.onClick {
            hideKeyboard()
            if (isValid()) {
                signInApiCall()
            }
        }

        mBinding.textForgotPassword?.onClick {
            hideKeyboard()
            Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_loginFragment_to_forgotPasswordFragment)
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
                    mBinding.editPassword,
                    EmptyValidator(),
                    getString(R.string.enter_password)
            )
            addValidate(
                    mBinding.editEmail,
                    EmailValidator(),
                    getString(R.string.enter_valid_email)
            )
        }.validate()
    }


    private fun signInApiCall() {
        val liveData = mViewModel.logInApi(LoginReq(mBinding.editEmail.value, mBinding.editPassword.value))
        liveData.observe(this, Observer {
            it.ifSuccess {
                context.startActivity<HomeActivity>()
                activity?.finish()
            }
        })
        mBinding.resource = liveData as LiveData<Resource<Any, APIError>>
    }

}
