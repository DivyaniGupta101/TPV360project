package com.tpv.android.ui.home.enrollment.dynamicform


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.libraries.places.widget.Autocomplete
import com.livinglifetechway.k4kotlin.core.androidx.hideKeyboard
import com.livinglifetechway.k4kotlin.core.androidx.toastNow
import com.livinglifetechway.k4kotlin.core.onClick
import com.livinglifetechway.k4kotlin.core.orFalse
import com.tpv.android.R
import com.tpv.android.databinding.*
import com.tpv.android.model.network.DynamicFormReq
import com.tpv.android.model.network.DynamicFormResp
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.ui.home.HomeActivity
import com.tpv.android.ui.home.enrollment.SetEnrollViewModel
import com.tpv.android.ui.home.enrollment.dynamicform.address.fillAddressFields
import com.tpv.android.ui.home.enrollment.dynamicform.address.isValid
import com.tpv.android.ui.home.enrollment.dynamicform.address.setField
import com.tpv.android.ui.home.enrollment.dynamicform.checkbox.isValid
import com.tpv.android.ui.home.enrollment.dynamicform.checkbox.setField
import com.tpv.android.ui.home.enrollment.dynamicform.fullname.isValid
import com.tpv.android.ui.home.enrollment.dynamicform.fullname.setField
import com.tpv.android.ui.home.enrollment.dynamicform.heading.setField
import com.tpv.android.ui.home.enrollment.dynamicform.label.setField
import com.tpv.android.ui.home.enrollment.dynamicform.multilineedittext.isValid
import com.tpv.android.ui.home.enrollment.dynamicform.multilineedittext.setField
import com.tpv.android.ui.home.enrollment.dynamicform.phone.isValid
import com.tpv.android.ui.home.enrollment.dynamicform.phone.setField
import com.tpv.android.ui.home.enrollment.dynamicform.radiobutton.isValid
import com.tpv.android.ui.home.enrollment.dynamicform.radiobutton.setField
import com.tpv.android.ui.home.enrollment.dynamicform.serviceandbillingaddress.fillAddressFields
import com.tpv.android.ui.home.enrollment.dynamicform.serviceandbillingaddress.isValid
import com.tpv.android.ui.home.enrollment.dynamicform.serviceandbillingaddress.setField
import com.tpv.android.ui.home.enrollment.dynamicform.singlelineedittext.isValid
import com.tpv.android.ui.home.enrollment.dynamicform.singlelineedittext.setField
import com.tpv.android.utils.enums.DynamicField
import com.tpv.android.utils.setupToolbar


class DynamicFormFragment : Fragment() {

    private lateinit var mBinding: FragmentDynamicFormBinding
    private lateinit var mViewModel: SetEnrollViewModel
    private var bindingList: ArrayList<Any> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_dynamic_form, container, false)

        mBinding.lifecycleOwner = this
        mViewModel = ViewModelProviders.of(this).get(SetEnrollViewModel::class.java)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    fun initialize() {

        mBinding.errorHandler = AlertErrorHandler(mBinding.root)

        setupToolbar(mBinding.toolbar, getString(R.string.customer_data), showBackIcon = true)
        getFormApiCall()

        mBinding.btnNext.onClick {
            hideKeyboard()

            val validList: ArrayList<Boolean> = ArrayList()

            if (bindingList.isNotEmpty()) {

                bindingList.forEach { view ->
                    validList.add(checkValid(view))
                }

                if (!validList.contains(false)) {
                    toastNow("good to go")
                }
            }
        }
    }

    private fun checkValid(view: Any): Boolean {
        return when (view) {
            is LayoutInputFullNameBinding -> {
                view.isValid(context)
            }
            is LayoutInputSingleLineEditTextBinding -> {
                view.isValid(context)
            }
            is LayoutInputPhoneNumberBinding -> {
                view.isValid(context)
            }
            is LayoutInputAddressBinding -> {
                view.isValid(context)
            }
            is LayoutInputServiceAndBillingAddressBinding -> {
                view.isValid(context)
            }
            is LayoutInputMultiLineEditTextBinding -> {
                view.isValid(context)
            }
            is LayoutInputRadioButtonBinding -> {
                view.isValid(context)
            }
            is LayoutInputCheckBoxBinding -> {
                view.isValid(context)
            }
            else -> {
                return true
            }
        }

    }


    private fun getFormApiCall() {
        val liveData = mViewModel.getDynamicForm(DynamicFormReq(clientid = "102",
                commodity = "Electric", programid = "716"))
        liveData.observe(this, Observer {
            it.ifSuccess {
                it?.forEach { resp ->
                    when (resp.type) {
                        DynamicField.FULLNAME.type -> {
                            setFieldsOfFullName(resp)
                        }
                        DynamicField.TEXTBOX.type -> {
                            setFieldsOfSinglLineEditText(resp)
                        }
                        DynamicField.EMAIL.type -> {
                            setFieldsOfSinglLineEditText(resp)
                        }
                        DynamicField.PHONENUMBER.type -> {
                            setFieldsOfPhoneNumber(resp)
                        }
                        DynamicField.HEADING.type -> {
                            setFieldsOfHeading(resp)
                        }
                        DynamicField.LABEL.type -> {
                            setFieldsOfLabel(resp)
                        }
                        DynamicField.ADDRESS.type -> {
                            setFieldsOfAddress(resp)
                        }
                        DynamicField.BOTHADDRESS.type -> {
                            setFieldOfBillingAndServiceAddress(resp)
                        }
                        DynamicField.TEXTAREA.type -> {
                            setFieldOfMultiLineEditText(resp)
                        }
                        DynamicField.RADIO.type -> {
                            setFieldOfRadioButton(resp)
                        }
                        DynamicField.CHECKBOX.type -> {
                            setFieldOfCheckBox(resp)
                        }
                    }
                }

            }

        })

        mBinding.resource = liveData as LiveData<Resource<Any, APIError>>
    }

    private fun setFieldsOfSinglLineEditText(response: DynamicFormResp) {
        val binding = DataBindingUtil.inflate<LayoutInputSingleLineEditTextBinding>(layoutInflater,
                R.layout.layout_input_single_line_edit_text,
                mBinding.fieldContainer,
                true)

        binding.setField(response)
        bindingList.add(binding)
    }


    private fun setFieldsOfFullName(response: DynamicFormResp) {
        val binding = DataBindingUtil.inflate<LayoutInputFullNameBinding>(layoutInflater,
                R.layout.layout_input_full_name,
                mBinding.fieldContainer,
                true)

        binding.setField(response)
        bindingList.add(binding)
    }

    private fun setFieldsOfHeading(response: DynamicFormResp) {
        val binding = DataBindingUtil.inflate<LayoutInputHeadingBinding>(layoutInflater,
                R.layout.layout_input_heading,
                mBinding.fieldContainer,
                true)

        binding.setField(response)
        bindingList.add(binding)
    }

    private fun setFieldsOfLabel(response: DynamicFormResp) {
        val binding = DataBindingUtil.inflate<LayoutInputLabelBinding>(layoutInflater,
                R.layout.layout_input_label,
                mBinding.fieldContainer,
                true)

        binding.setField(response)
        bindingList.add(binding)
    }

    private fun setFieldsOfPhoneNumber(response: DynamicFormResp) {
        val binding = DataBindingUtil.inflate<LayoutInputPhoneNumberBinding>(layoutInflater,
                R.layout.layout_input_phone_number,
                mBinding.fieldContainer,
                true)

        binding.setField(response, mViewModel, mBinding)
        bindingList.add(binding)
    }

    private fun setFieldsOfAddress(response: DynamicFormResp) {
        val binding = DataBindingUtil.inflate<LayoutInputAddressBinding>(layoutInflater,
                R.layout.layout_input_address,
                mBinding.fieldContainer,
                true)
        binding.setField(response)
        bindingList.add(binding)
    }


    private fun setFieldOfBillingAndServiceAddress(response: DynamicFormResp) {
        val binding = DataBindingUtil.inflate<LayoutInputServiceAndBillingAddressBinding>(layoutInflater,
                R.layout.layout_input_service_and_billing_address,
                mBinding.fieldContainer,
                true)
        binding.setField(response)
        bindingList.add(binding)
    }

    private fun setFieldOfMultiLineEditText(response: DynamicFormResp) {
        val binding = DataBindingUtil.inflate<LayoutInputMultiLineEditTextBinding>(layoutInflater,
                R.layout.layout_input_multi_line_edit_text,
                mBinding.fieldContainer,
                true)

        binding.setField(response)
        bindingList.add(binding)
    }

    private fun setFieldOfRadioButton(response: DynamicFormResp) {
        val binding = DataBindingUtil.inflate<LayoutInputRadioButtonBinding>(layoutInflater,
                R.layout.layout_input_radio_button,
                mBinding.fieldContainer,
                true)

        binding.setField(response)
        bindingList.add(binding)
    }

    private fun setFieldOfCheckBox(response: DynamicFormResp) {
        val binding = DataBindingUtil.inflate<LayoutInputCheckBoxBinding>(layoutInflater,
                R.layout.layout_input_check_box,
                mBinding.fieldContainer,
                true)

        binding.setField(response)
        bindingList.add(binding)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == HomeActivity.ADDRESS_REQUEST_CODE) {

                val id = HomeActivity.ADDRESS_REQUEST_CODE - 5000
                HomeActivity.ADDRESS_REQUEST_CODE = 5000

                bindingList.forEach { binding ->
                    when (binding) {
                        is LayoutInputAddressBinding -> {
                            if (binding.item?.id?.equals(id).orFalse()) {
                                binding.fillAddressFields(data?.let { Autocomplete.getPlaceFromIntent(it) })
                            }
                        }
                    }
                }

            } else if (requestCode == HomeActivity.BILLING_ADDRESS_REQUEST_CODE) {

                val id = HomeActivity.BILLING_ADDRESS_REQUEST_CODE - 5000
                HomeActivity.BILLING_ADDRESS_REQUEST_CODE = 5000

                bindingList.forEach { binding ->
                    when (binding) {
                        is LayoutInputServiceAndBillingAddressBinding -> {
                            if (binding.item?.id?.equals(id).orFalse()) {
                                binding.fillAddressFields(data?.let { Autocomplete.getPlaceFromIntent(it) }, false)
                            }
                        }
                    }
                }
            } else if (requestCode == HomeActivity.SERVICE_ADDRESS_REQUEST_CODE) {

                val id = HomeActivity.SERVICE_ADDRESS_REQUEST_CODE - 5000
                HomeActivity.SERVICE_ADDRESS_REQUEST_CODE = 5000

                bindingList.forEach { binding ->
                    when (binding) {
                        is LayoutInputServiceAndBillingAddressBinding -> {
                            if (binding.item?.id?.equals(id).orFalse()) {
                                binding.fillAddressFields(data?.let { Autocomplete.getPlaceFromIntent(it) }, true)
                            }
                        }
                    }
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }
}
