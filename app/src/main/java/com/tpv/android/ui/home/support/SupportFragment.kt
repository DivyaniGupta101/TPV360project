package com.tpv.android.ui.home.support

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
import com.tpv.android.databinding.FragmentSupportBinding
import com.tpv.android.helper.Pref
import com.tpv.android.model.network.TicketReq
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.utils.setupToolbar
import com.tpv.android.utils.validation.EmptyValidator
import com.tpv.android.utils.validation.TextInputValidationErrorHandler
import com.tpv.android.utils.validation.Validator

/**
 * A simple [Fragment] subclass.
 */
class SupportFragment : Fragment() {
    lateinit var mBinding: FragmentSupportBinding
    lateinit var mViewModel: SupportViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_support, container, false)
        mViewModel = ViewModelProviders.of(this).get(SupportViewModel::class.java)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.errorHandler = AlertErrorHandler(view)
        initalize()
    }

    private fun initalize() {
        setupToolbar(mBinding.toolbar, getString(R.string.support), showBackIcon = true)
        mBinding.btnSubmit.onClick {
            if (isValid()) {
                hideKeyboard()

                val liveData = mViewModel.getTicket(ticketReq = TicketReq(description = mBinding.editDescription.value,
                        email = Pref.user?.email,
                        subject = mBinding.editSubject.value))
                liveData.observe(this@SupportFragment, Observer {
                    it.ifSuccess {
                        Navigation.findNavController(mBinding.root).navigate(R.id.action_supportFragment_to_supportSuccessFragment)
                    }
                })

                mBinding.resource = liveData as LiveData<Resource<Any, APIError>>
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
