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
import com.livinglifetechway.k4kotlin.core.onClick
import com.livinglifetechway.k4kotlin.core.startActivity
import com.livinglifetechway.k4kotlin.core.value
import com.tpv.android.BuildConfig
import com.tpv.android.R
import com.tpv.android.databinding.FragmentLoginBinding
import com.tpv.android.model.LoginReq
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.network.resources.APIError
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.ifSuccess
import com.tpv.android.ui.home.HomeActivity

/**
 * A simple [Fragment] subclass.
 */
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

        if (BuildConfig.DEBUG) {
            mBinding.textEmail.setText("testingusermm@mailinator.com")
            mBinding.textPassword.setText("Admin1!")

        }

        mBinding.btnStart.onClick {
            signInApi()
        }

        mBinding.textForgotPassword?.onClick {
            Navigation.findNavController(mBinding.root).navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }
    }

    private fun signInApi() {

            //TODO Remove redirection of Home screen from here and replace it in success call
        mBinding.errorHandler = AlertErrorHandler(mBinding.root) {
            context.startActivity<HomeActivity>()
            activity?.finish()
        }
        val liveData = mViewModel.logInApi(LoginReq(mBinding.textEmail.value, mBinding.textPassword.value))
        liveData.observe(this, Observer {
            it.ifSuccess {

            }
        })
        mBinding.resource = liveData as LiveData<Resource<Any, APIError>>
    }

}
