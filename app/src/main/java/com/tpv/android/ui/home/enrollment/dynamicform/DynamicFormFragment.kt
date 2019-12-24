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
import com.tpv.android.helper.OnBackPressCallBack
import com.tpv.android.model.network.DynamicFormResp
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.ui.home.HomeActivity
import com.tpv.android.ui.home.enrollment.SetEnrollViewModel
import com.tpv.android.ui.home.enrollment.dynamicform.address.fillAddressFields
import com.tpv.android.ui.home.enrollment.dynamicform.address.isValid
import com.tpv.android.ui.home.enrollment.dynamicform.address.setField
import com.tpv.android.ui.home.enrollment.dynamicform.checkbox.isValid
import com.tpv.android.ui.home.enrollment.dynamicform.checkbox.setField
import com.tpv.android.ui.home.enrollment.dynamicform.email.isValid
import com.tpv.android.ui.home.enrollment.dynamicform.email.setField
import com.tpv.android.ui.home.enrollment.dynamicform.fullname.isValid
import com.tpv.android.ui.home.enrollment.dynamicform.fullname.setField
import com.tpv.android.ui.home.enrollment.dynamicform.heading.setField
import com.tpv.android.ui.home.enrollment.dynamicform.infotext.setField
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


class DynamicFormFragment : Fragment(), OnBackPressCallBack {

    private lateinit var mBinding: FragmentDynamicFormBinding
    private lateinit var mViewModel: SetEnrollViewModel
    private var bindingList: ArrayList<Any> = ArrayList()
    private var totalPage: Int = 1
    private var hasNext: Boolean = false
    private var currentPage = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_dynamic_form, container, false)

        mBinding.lifecycleOwner = this
        activity?.let { mViewModel = ViewModelProviders.of(it).get(SetEnrollViewModel::class.java) }

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    override fun handleOnBackPressed(): Boolean {
        saveOldData()
        return true
    }

    fun initialize() {

        mBinding.errorHandler = AlertErrorHandler(mBinding.root)

        currentPage = arguments?.let { DynamicFormFragmentArgs.fromBundle(it) }?.item.orZero()
        totalPage = mViewModel.duplicatePageMap?.size.orZero()

        setupToolbar(mBinding.toolbar, getString(R.string.customer_data), showBackIcon = true, backIconClickListener = {
            saveOldData()
        })

        //Check next page is Available or not
        if (currentPage == totalPage) {
            hasNext = false
        } else hasNext = currentPage < totalPage.orZero()

        //Inflate all the views
        inflateViews()

        mBinding.btnNext.onClick {

            hideKeyboard()

            val validList: ArrayList<Boolean> = ArrayList()
            bindingList.forEach { view ->
                validList.add(checkValid(view))
            }

            //Check if all validation is true then check have next page then
            //Load this page again else sent to client info screen
            if (!validList.contains(false)) {
                mViewModel.formPageMap?.put(currentPage, mViewModel.duplicatePageMap?.get(currentPage).orEmpty())
                if (hasNext) {
                    currentPage += 1
                    Navigation.findNavController(mBinding.root).navigateSafe(DynamicFormFragmentDirections.actionDynamicFormFragmentSelf(currentPage))
                } else {
                    mViewModel.dynamicFormData.clear()
                    for (key in 1..mViewModel.formPageMap?.size.orZero()) {
                        mViewModel.dynamicFormData.addAll(mViewModel.formPageMap?.get(key).orEmpty())
                    }
                    Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_dynamicFormFragment_to_clientInfoFragment)
                }
            }
        }
    }

    private fun saveOldData() {
        mViewModel.duplicatePageMap?.put(currentPage, mViewModel.formPageMap?.get(currentPage).orEmpty())
    }

    /**
     * Check validation of inflated views
     */
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
            is LayoutInputEmailAddressBinding -> {
                view.isValid(context)
            }
            else -> {
                return true
            }
        }

    }


    private fun inflateViews() {

        //Handle page indicator view
        for (pageNumber in 1..totalPage.orZero()) {
            val binding = DataBindingUtil.inflate<LayoutHighlightIndicatorBinding>(layoutInflater,
                    R.layout.layout_highlight_indicator,
                    mBinding.indicatorContainer,
                    true)
            binding.currentPage = currentPage
            binding.pageNumber = pageNumber
        }


        mViewModel.duplicatePageMap?.get(currentPage)?.forEach { response ->
            when (response.type) {
                DynamicField.FULLNAME.type -> {
                    setFieldsOfFullName(response)
                }
                DynamicField.TEXTBOX.type -> {
                    setFieldsOfSinglLineEditText(response)
                }
                DynamicField.EMAIL.type -> {
                    setFieldsOfEmailAddress(response)
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
                DynamicField.TEXT.type -> {
                    setFieldOfMessageInfo(response)
                }
            }
        }

    }

    /**
     * Inflate email address view
     */
    private fun setFieldsOfEmailAddress(response: DynamicFormResp) {
        val binding = DataBindingUtil.inflate<LayoutInputEmailAddressBinding>(layoutInflater,
                R.layout.layout_input_email_address,
                mBinding.fieldContainer,
                true)

        binding.setField(response)
        bindingList.add(binding)
    }

    /**
     * Inflate single line edit text view
     */
    private fun setFieldsOfSinglLineEditText(response: DynamicFormResp) {
        val binding = DataBindingUtil.inflate<LayoutInputSingleLineEditTextBinding>(layoutInflater,
                R.layout.layout_input_single_line_edit_text,
                mBinding.fieldContainer,
                true)

        binding.setField(response)
        bindingList.add(binding)
    }

    /**
     * Inflate full name view
     */
    private fun setFieldsOfFullName(response: DynamicFormResp) {
        val binding = DataBindingUtil.inflate<LayoutInputFullNameBinding>(layoutInflater,
                R.layout.layout_input_full_name,
                mBinding.fieldContainer,
                true)

        binding.setField(response)
        bindingList.add(binding)
    }

    /**
     * Inflate heading view
     */
    private fun setFieldsOfHeading(response: DynamicFormResp) {
        val binding = DataBindingUtil.inflate<LayoutInputHeadingBinding>(layoutInflater,
                R.layout.layout_input_heading,
                mBinding.fieldContainer,
                true)

        binding.setField(response)
        bindingList.add(binding)
    }

    /**
     * Inflate label view
     */
    private fun setFieldsOfLabel(response: DynamicFormResp) {
        val binding = DataBindingUtil.inflate<LayoutInputLabelBinding>(layoutInflater,
                R.layout.layout_input_label,
                mBinding.fieldContainer,
                true)

        binding.setField(response)
        bindingList.add(binding)
    }

    /**
     * Inflate phoneNumber view
     */
    private fun setFieldsOfPhoneNumber(response: DynamicFormResp) {
        val binding = DataBindingUtil.inflate<LayoutInputPhoneNumberBinding>(layoutInflater,
                R.layout.layout_input_phone_number,
                mBinding.fieldContainer,
                true)

        binding.setField(response, mViewModel, mBinding)
        bindingList.add(binding)
    }

    /**
     * Inflate address view
     */
    private fun setFieldsOfAddress(response: DynamicFormResp) {
        val binding = DataBindingUtil.inflate<LayoutInputAddressBinding>(layoutInflater,
                R.layout.layout_input_address,
                mBinding.fieldContainer,
                true)
        binding.setField(response)
        bindingList.add(binding)
    }

    /**
     * Inflate billing and service address view
     */
    private fun setFieldOfBillingAndServiceAddress(response: DynamicFormResp) {
        val binding = DataBindingUtil.inflate<LayoutInputServiceAndBillingAddressBinding>(layoutInflater,
                R.layout.layout_input_service_and_billing_address,
                mBinding.fieldContainer,
                true)
        binding.setField(response)
        bindingList.add(binding)
    }

    /**
     * Inflate multi line edit text view
     */
    private fun setFieldOfMultiLineEditText(response: DynamicFormResp) {
        val binding = DataBindingUtil.inflate<LayoutInputMultiLineEditTextBinding>(layoutInflater,
                R.layout.layout_input_multi_line_edit_text,
                mBinding.fieldContainer,
                true)

        binding.setField(response)
        bindingList.add(binding)
    }

    /**
     * Inflate radio button view
     */
    private fun setFieldOfRadioButton(response: DynamicFormResp) {
        val binding = DataBindingUtil.inflate<LayoutInputRadioButtonBinding>(layoutInflater,
                R.layout.layout_input_radio_button,
                mBinding.fieldContainer,
                true)

        binding.setField(response)
        bindingList.add(binding)
    }

    /**
     * Inflate check box view
     */
    private fun setFieldOfCheckBox(response: DynamicFormResp) {
        val binding = DataBindingUtil.inflate<LayoutInputCheckBoxBinding>(layoutInflater,
                R.layout.layout_input_check_box,
                mBinding.fieldContainer,
                true)

        binding.setField(response)
        bindingList.add(binding)

    }

    /**
     * Inflate spinner(dropdown) view
     */
    private fun setFieldOfSpinner(response: DynamicFormResp) {
        val binding = DataBindingUtil.inflate<LayoutInputSpinnerBinding>(layoutInflater,
                R.layout.layout_input_spinner,
                mBinding.fieldContainer,
                true)

        binding.setField(response)
        bindingList.add(binding)
    }


    private fun setFieldOfMessageInfo(response: DynamicFormResp) {
        val binding = DataBindingUtil.inflate<LayoutInputTextInfoBinding>(layoutInflater,
                R.layout.layout_input_text_info,
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

                //Get binding from list if layout is address then check id is same
                // Then set addressComponent in addressFields
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

                //Get binding from list if layout is serviceAndBillingAddress then check id is same
                // Then set addressComponent in billingAddressFields
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

                //Get binding from list if layout is serviceAndBillingAddress then check id is same
                // Then set addressComponent in serviceAddressFields
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
