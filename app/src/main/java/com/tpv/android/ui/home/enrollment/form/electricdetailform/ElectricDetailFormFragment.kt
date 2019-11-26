package com.tpv.android.ui.home.enrollment.form.electricdetailform


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
import com.tpv.android.databinding.FragmentElectricDetailFormBinding
import com.tpv.android.ui.home.enrollment.SetEnrollViewModel
import com.tpv.android.utils.Plan
import com.tpv.android.utils.navigateSafe
import com.tpv.android.utils.setupToolbar
import com.tpv.android.utils.validation.EmptyValidator
import com.tpv.android.utils.validation.TextInputValidationErrorHandler
import com.tpv.android.utils.validation.Validator

/**
 * A simple [Fragment] subclass.
 */
class ElectricDetailFormFragment : Fragment() {
    private lateinit var mBinding: FragmentElectricDetailFormBinding
    private lateinit var mViewModel: SetEnrollViewModel
    private var mSelectedRadioButton = "No"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_electric_detail_form, container, false)
        activity?.let { mViewModel = ViewModelProviders.of(it).get(SetEnrollViewModel::class.java) }
        return mBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar(mBinding.toolbar, getString(R.string.customer_data), showBackIcon = true)
        mBinding.item = mViewModel.customerData
        mBinding.viewModel = mViewModel

        mBinding.radioYes?.onClick()
        {
            mBinding.editServiceAddress.value = mBinding.editBillingAddress.value
            mBinding.editServiceAddress.isEnabled = false
            mBinding.editServiceAddress.setTextColor(context.color(R.color.colorSecondaryText))
        }

        mBinding.radioNo?.onClick()
        {
            mBinding.editServiceAddress.value = ""
            mBinding.editServiceAddress.isEnabled = true
            mBinding.editServiceAddress.setTextColor(context.color(R.color.colorPrimaryText))
        }

        mBinding.editBillingAddress.addTextChangedListener(
                object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        if (mBinding.radioYes.isChecked) {
                            mBinding.editServiceAddress.value = mBinding.editBillingAddress.value
                        }
                    }

                })


        mBinding.radioGroup.setOnCheckedChangeListener()
        { group, checkedId ->
            mSelectedRadioButton = group.findViewById<RadioButton>(checkedId).text.toString()
        }


        mBinding.btnNext.onClick()
        {
            if (isValid()) {
                hideKeyboard()
                setValueInViewModel()
                Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_electricDetailFormFragment_to_clientInfoFragment)
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
                    mBinding.editZipCode,
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
        mViewModel.isElectricServiceAddressSame = if (mSelectedRadioButton.equals(getString(R.string.yes))) true else false

        when (mViewModel.planType) {
            Plan.ELECTRICFUEL.value -> {
                mViewModel.customerData.apply {
                    billingFirstName = mBinding.editBillingFirstName.value
                    billingMiddleInitial = mBinding.editBillingMiddleName.value
                    billingLastName = mBinding.editBillingLastName.value
                    billingAddress = mBinding.editBillingAddress.value
                    billingZip = mBinding.editZipCode.value
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
                    electricBillingFirstName = mBinding.editBillingFirstName.value
                    electricBillingMiddleInitial = mBinding.editBillingMiddleName.value
                    electricBillingLastName = mBinding.editBillingLastName.value
                    electricBillingAddress = mBinding.editBillingAddress.value
                    electricBillingAddress2 = ""
                    electricBillingCity = "Amherst"
                    electricBillingState = "MA"
                    electricBillingZip = mBinding.editZipCode.value
                    serviceAddress = mBinding.editServiceAddress.value
                    serviceAddress2 = ""
                    serviceCity = "Amherst"
                    serviceState = "MA"
                    serviceZip = mBinding.editServiceZipCode.value
                    electricAccountNumber = mBinding.editAccountNumber.value
                }
            }
        }
    }
}
