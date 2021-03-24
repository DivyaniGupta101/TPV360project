package com.tpv.android.ui.salesagent.home.support

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.livinglifetechway.k4kotlin.core.androidx.hideKeyboard
import com.livinglifetechway.k4kotlin.core.hide
import com.livinglifetechway.k4kotlin.core.onClick
import com.livinglifetechway.k4kotlin.core.setItems
import com.livinglifetechway.k4kotlin.core.value
import com.tpv.android.R
import com.tpv.android.databinding.FragmentSupportBinding
import com.tpv.android.helper.Pref
import com.tpv.android.model.network.TicketReq
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.utils.infoDialog
import com.tpv.android.utils.setupToolbar
import com.tpv.android.utils.validation.EmptyValidator
import com.tpv.android.utils.validation.TextInputValidationErrorHandler
import com.tpv.android.utils.validation.Validator
import kotlinx.android.synthetic.main.fragment_support.*

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
        mBinding.spinner.setItems(arrayListOf(getString(R.string.low),
                getString(R.string.medium),
                getString(R.string.high),
                getString(R.string.urgent)))

        mBinding.editSubject.doOnTextChanged { text, start, count, after ->
            mBinding.textSuccessful.hide()
        }
        mBinding.editDescription.doOnTextChanged { text, start, count, after ->
            mBinding.textSuccessful.hide()
        }

        mBinding.btnSubmit.onClick {
            if (isValid()) {
                hideKeyboard()
                createTicketCall()
            }
        }
    }

    private fun createTicketCall() {
        val liveData = mViewModel.getTicket(
                ticketReq = TicketReq(description = mBinding.editDescription.value,
                        email = mBinding.editEmail.value,
                        subject = mBinding.editSubject.value,
                        priority = mBinding.spinner.selectedItemPosition + 1))
        liveData.observe(this@SupportFragment, Observer {
            it.ifSuccess {
                context?.infoDialog(getString(R.string.thank_you), getString(R.string.ticket_recived_msg), setOnButtonClickListener =
                {
                    Navigation.findNavController(mBinding.root).navigateUp()
                },showImageError = false)

//                Navigation.findNavController(mBinding.root).navigate(R.id.action_supportFragment_to_supportSuccessFragment)
            }
        })

        mBinding.resource = liveData as LiveData<Resource<Any, APIError>>
    }

    fun isValid(): Boolean {

        return Validator(TextInputValidationErrorHandler()) {
            addValidate(
                    mBinding.editSubject,
                    EmptyValidator(),
                    context?.getString(R.string.enter_subject)
            )
            addValidate(
                    mBinding.editEmail,
                    EmptyValidator(),
                    context?.getString(R.string.enter_email)
            )
            addValidate(
                    mBinding.editDescription,
                    EmptyValidator(),
                    context?.getString(R.string.enter_description)
            )
        }.validate()
    }
}
