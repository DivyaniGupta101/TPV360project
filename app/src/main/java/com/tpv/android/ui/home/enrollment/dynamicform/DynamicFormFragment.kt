package com.tpv.android.ui.home.enrollment.dynamicform


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.google.android.libraries.places.widget.Autocomplete
import com.livinglifetechway.k4kotlin.core.androidx.hideKeyboard
import com.livinglifetechway.k4kotlin.core.onClick
import com.livinglifetechway.k4kotlin.core.orFalse
import com.livinglifetechway.k4kotlin.core.orZero
import com.tpv.android.R
import com.tpv.android.databinding.*
import com.tpv.android.model.network.DynamicFormResp
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
import com.tpv.android.ui.home.enrollment.dynamicform.spinner.setField
import com.tpv.android.utils.enums.DynamicField
import com.tpv.android.utils.navigateSafe
import com.tpv.android.utils.setupToolbar


class DynamicFormFragment : Fragment() {

    private lateinit var mBinding: FragmentDynamicFormBinding
    private lateinit var mViewModel: SetEnrollViewModel
    private var bindingList: ArrayList<Any> = ArrayList()
    private var totalPage: Int? = null
    private var hasNext: Boolean = false

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
        setupToolbar(mBinding.toolbar, getString(R.string.customer_data), showBackIcon = true)

        totalPage = mViewModel.dynamicForm?.size

        if (mViewModel.dynamicFormCurrentPage == totalPage) {
            hasNext = false
        } else if (mViewModel.dynamicFormCurrentPage < totalPage.orZero()) {
            hasNext = true
        } else {
            hasNext = false
        }


        inflateViews(totalPage)


        mBinding.btnNext.onClick {
            hideKeyboard()

            val validList: ArrayList<Boolean> = ArrayList()

            bindingList.forEach { view ->
                validList.add(checkValid(view))
            }

            if (!validList.contains(false)) {
                if (hasNext) {
                    mViewModel.dynamicFormCurrentPage = mViewModel.dynamicFormCurrentPage + 1
                    Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_dynamicFormFragment_to_clientInfoFragment)
                } else {
                    Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_dynamicFormFragment_self)
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


    private fun inflateViews(totalPage: Int?) {

        for (pageNumber in 0..totalPage.orZero()) {
            val binding = DataBindingUtil.inflate<LayoutHighlightIndicatorBinding>(layoutInflater,
                    R.layout.layout_highlight_indicator,
                    mBinding.indicatorContainer,
                    true)
            binding.currentPage = mViewModel.dynamicFormCurrentPage
            binding.pageNumber = pageNumber
        }




        mViewModel.dynamicForm?.get(mViewModel.dynamicFormCurrentPage)?.forEach { response ->
            when (response.type) {
                DynamicField.FULLNAME.type -> {
                    setFieldsOfFullName(response)
                }
                DynamicField.TEXTBOX.type -> {
                    setFieldsOfSinglLineEditText(response)
                }
                DynamicField.EMAIL.type -> {
                    setFieldsOfSinglLineEditText(response)
                }
                DynamicField.PHONENUMBER.type -> {
                    setFieldsOfPhoneNumber(response)
                }
                DynamicField.HEADING.type -> {
                    setFieldsOfHeading(response)
                }
                DynamicField.LABEL.type -> {
                    setFieldsOfLabel(response)
                }
                DynamicField.ADDRESS.type -> {
                    setFieldsOfAddress(response)
                }
                DynamicField.BOTHADDRESS.type -> {
                    setFieldOfBillingAndServiceAddress(response)
                }
                DynamicField.TEXTAREA.type -> {
                    setFieldOfMultiLineEditText(response)
                }
                DynamicField.RADIO.type -> {
                    setFieldOfRadioButton(response)
                }
                DynamicField.CHECKBOX.type -> {
                    setFieldOfCheckBox(response)
                }
                DynamicField.SELECTBOX.type -> {
                    setFieldOfSpinner(response)
                }
            }
        }

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

    private fun setFieldOfSpinner(response: DynamicFormResp) {
        val binding = DataBindingUtil.inflate<LayoutInputSpinnerBinding>(layoutInflater,
                R.layout.layout_input_spinner,
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
