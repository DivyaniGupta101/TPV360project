package com.tpv.android.ui.home.enrollment.form.gasdetailform


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.livinglifetechway.k4kotlin.core.androidx.color
import com.livinglifetechway.k4kotlin.core.androidx.hideKeyboard
import com.livinglifetechway.k4kotlin.core.onClick
import com.livinglifetechway.k4kotlin.core.value
import com.tpv.android.R
import com.tpv.android.databinding.FragmentGasDetailFormBinding
import com.tpv.android.ui.home.enrollment.SetEnrollViewModel
import com.tpv.android.utils.Plan
import com.tpv.android.utils.navigateSafe
import com.tpv.android.utils.setupToolbar
import com.tpv.android.utils.validation.EmptyValidator
import com.tpv.android.utils.validation.TextInputValidationErrorHandler
import com.tpv.android.utils.validation.Validator

class GasDetailFormFragment : Fragment() {
    private lateinit var mBinding: FragmentGasDetailFormBinding
    private lateinit var mViewModel: SetEnrollViewModel
    private var mSelectedRadioButton = "No"
    private var mCountryCodeList = arrayListOf("+1")


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_gas_detail_form, container, false)
        activity?.let { mViewModel = ViewModelProviders.of(it).get(SetEnrollViewModel::class.java) }
        mBinding.viewModel = mViewModel
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar(mBinding.toolbar, getString(R.string.customer_data), showBackIcon = true)


        mBinding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            mSelectedRadioButton = group.findViewById<RadioButton>(checkedId).text.toString()
        }


        mBinding.item = mViewModel.customerData

        mBinding.radioYes?.onClick {
            mBinding.editBillingAddress.value = mBinding.editServiceAddress.value
            mBinding.editBillingZipCode.value = mBinding.editServiceZipCode.value
            mBinding.editBillingAddress.isEnabled = false
            mBinding.editBillingZipCode.isEnabled = false
            mBinding.editBillingAddress.setTextColor(context.color(R.color.colorSecondaryText))
            mBinding.editBillingZipCode.setTextColor(context.color(R.color.colorSecondaryText))
        }

        mBinding.radioNo?.onClick {
            mBinding.editBillingAddress.value = ""
            mBinding.editBillingZipCode.value = ""
            mBinding.editBillingAddress.isEnabled = true
            mBinding.editBillingZipCode.isEnabled = true
            mBinding.editBillingAddress.setTextColor(context.color(R.color.colorPrimaryText))
            mBinding.editBillingZipCode.setTextColor(context.color(R.color.colorPrimaryText))
        }

        mBinding.editServiceAddress.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (mBinding.radioYes.isChecked) {
                    mBinding.editBillingAddress.value = mBinding.editServiceAddress.value
                }
            }

        })


        mBinding.btnNext.onClick {
            hideKeyboard()
            if (isValid()) {
                setValueInViewModel()

                when (mViewModel.planType) {
                    Plan.GASFUEL.value -> {
                        Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_gasDetailFormFragment_to_clientInfoFragment)
                    }
                    Plan.DUALFUEL.value -> {
                        Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_gasDetailFormFragment_to_electricDetailFormFragment)
                    }
                }
            }
        }
    }

    private fun isValid(): Boolean {
        return Validator(TextInputValidationErrorHandler()) {
            addValidate(
                    mBinding.editBillingFirstName,
                    EmptyValidator(),
                    getString(R.string.enter_billing_first_name)
            )
            addValidate(
                    mBinding.editBillingMiddleName,
                    EmptyValidator(),
                    getString(R.string.enter_billing_middle_name)
            )
            addValidate(
                    mBinding.editBillingLastName,
                    EmptyValidator(),
                    getString(R.string.enter_billing_last_name)
            )
            addValidate(
                    mBinding.editBillingAddress,
                    EmptyValidator(),
                    getString(R.string.enter_billing_address)
            )
            addValidate(
                    mBinding.editBillingZipCode,
                    EmptyValidator(),
                    getString(R.string.enter_zipcode)
            )
            addValidate(
                    mBinding.editServiceAddress,
                    EmptyValidator(),
                    getString(R.string.enter_service_address)
            )
            addValidate(
                    mBinding.editServiceZipCode,
                    EmptyValidator(),
                    getString(R.string.enter_service_zipcode)
            )
            addValidate(
                    mBinding.editAccountNumber,
                    EmptyValidator(),
                    getString(R.string.enter_account_number)
            )
        }.validate()
    }

    private fun setValueInViewModel() {
        mViewModel.isGasServiceAddressSame = if (mSelectedRadioButton == getString(R.string.yes)) true else false

        when (mViewModel.planType) {
            Plan.GASFUEL.value -> {
                mViewModel.customerData.apply {
                    billingFirstName = mBinding.editBillingFirstName.value
                    billingMiddleInitial = mBinding.editBillingMiddleName.value
                    billingLastName = mBinding.editBillingLastName.value
                    billingAddress = mBinding.editBillingAddress.value
                    billingZip = mBinding.editBillingZipCode.value
                    isTheBillingAddressTheSameAsTheServiceAddress = mSelectedRadioButton
                    billingAddress2 = ""
                    serviceAddress = mBinding.editServiceAddress.value
                    serviceAddress2 = ""
                    serviceZip = mBinding.editServiceZipCode.value
                    accountNumber = mBinding.editAccountNumber.value
                    billingCity = "Amherst"
                    billingState = "MA"
                    serviceState = "MA"
                    serviceCity = "Amherst"
                }
            }

            Plan.DUALFUEL.value -> {
                mViewModel.customerData.apply {
                    gasBillingAddress = mBinding.editBillingAddress.value
                    gasBillingAddress2 = ""
                    gasServiceAddress = mBinding.editServiceAddress.value
                    gasServiceAddress2 = ""
                    gasBillingFirstName = mBinding.editBillingFirstName.value
                    gasBillingMiddleInitial = mBinding.editBillingMiddleName.value
                    gasBillingLastName = mBinding.editBillingLastName.value
                    gasBillingCity = "Amherst"
                    gasBillingState = "MA"
                    gasBillingZip = mBinding.editBillingZipCode.value
                    gasServiceZip = mBinding.editServiceZipCode.value
                    gasServiceCity = "Amherst"
                    gasServiceState = "MA"
                    isTheBillingAddressTheSameAsTheServiceAddress = mSelectedRadioButton
                    gasAccountNumber = mBinding.editAccountNumber.value
                }
            }
        }
    }


}
