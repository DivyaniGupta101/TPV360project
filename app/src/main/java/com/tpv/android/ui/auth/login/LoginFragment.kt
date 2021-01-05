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
import com.tpv.android.ui.client.ui.ClientHomeActivity
import com.tpv.android.ui.salesagent.home.HomeActivity
import com.tpv.android.utils.AppConstant
import com.tpv.android.utils.navigateSafe
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
        mBinding.textInputPassword.isHintEnabled = false
        mBinding.textInputEmail.isHintEnabled = false

        if (Pref.token != null) {
            if (Pref.user?.accessLevel == AppConstant.CLIENT) {
                context.startActivity<ClientHomeActivity>()
                activity?.finish()
            }
            if (Pref.user?.accessLevel == AppConstant.SALESAGENT) {
                context.startActivity<HomeActivity>()
                activity?.finish()
            }

        }
        if (BuildConfig.DEBUG) {
            if (BuildConfig.FLAVOR.equals("NewDevNotAllow") ||
                    BuildConfig.FLAVOR.equals("NewDevAllow")) {
                mBinding.editEmail.setText("mansi.d2d_agent+1@gmail.com")
                mBinding.editPassword.setText("tpv@123")
            }

            if (BuildConfig.FLAVOR.equals("NewDemoNotAllow") ||
                    BuildConfig.FLAVOR.equals("NewDemoAllow")) {
                mBinding.editEmail.setText("mansi.d2d_agent+1@gmail.com")
                mBinding.editPassword.setText("tpv@123")
            }
            if (BuildConfig.FLAVOR.equals("ProdNotAllow") ||
                    BuildConfig.FLAVOR.equals("ProdAllow")) {
                mBinding.editEmail.setText("mansi.d2d_agent+1@gmail.com")
                mBinding.editPassword.setText("tpv@123")
            }
            if (BuildConfig.FLAVOR.equals("TpvTestAllow") ||
                    BuildConfig.FLAVOR.equals("TpvTestNotAllow")) {
                mBinding.editEmail.setText("riddhi.client_admin+1@gmail.com")
                mBinding.editPassword.setText("tpv@123")
            }
            if (BuildConfig.FLAVOR.equals("TPV360Allow") ||
                    BuildConfig.FLAVOR.equals("TpvTestNotAllow")) {
                mBinding.editEmail.setText("abhilash.badda+d2dapp@gmail.com")
                mBinding.editPassword.setText("123456")
            }

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
                    getString(R.string.please_enter_username)
            )
            addValidate(
                    mBinding.editPassword,
                    EmptyValidator(),
                    getString(R.string.enter_password)
            )
//            addValidate(
//                    mBinding.editEmail,
//                    EmailValidator(),
//                    getString(R.string.enter_valid_email)
//            )
        }.validate()
    }


    private fun signInApiCall() {
        val liveData = mViewModel.logInApi(LoginReq(mBinding.editEmail.value, mBinding.editPassword.value))
        liveData.observe(this, Observer {
            it.ifSuccess {
                if (it?.accessLevel == AppConstant.SALESAGENT) {
                    context.startActivity<HomeActivity>()
                    activity?.finish()
                }
                if (it?.accessLevel == AppConstant.CLIENT) {
                    context.startActivity<ClientHomeActivity>()
                    activity?.finish()
                }
            }
        })
        mBinding.resource = liveData as LiveData<Resource<Any, APIError>>
    }

}
