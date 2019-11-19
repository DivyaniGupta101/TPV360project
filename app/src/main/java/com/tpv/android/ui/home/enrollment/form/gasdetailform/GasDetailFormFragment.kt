package com.tpv.android.ui.home.enrollment.form.gasdetailform


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.livinglifetechway.k4kotlin.core.androidx.hideKeyboard
import com.livinglifetechway.k4kotlin.core.onClick
import com.livinglifetechway.k4kotlin.core.value
import com.tpv.android.R
import com.tpv.android.databinding.FragmentGasDetailFormBinding
import com.tpv.android.ui.home.enrollment.SetEnrollViewModel
import com.tpv.android.utils.Plan
import com.tpv.android.utils.navigateSafe
import com.tpv.android.utils.setupToolbar

/**
 * A simple [Fragment] subclass.
 */
class GasDetailFormFragment : Fragment() {
    private lateinit var mBinding: FragmentGasDetailFormBinding
    private lateinit var mViewModel: SetEnrollViewModel
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

        mBinding.item = mViewModel.serviceDetail

        mBinding.checkBoxYes?.onClick {
            mBinding.editServiceAddress.value = mBinding.editBillingAddress.value
            mBinding.editServiceZipCode.value = mBinding.editZipCode.value
        }

        mBinding.checkBoxNo?.onClick {
            mBinding.editServiceAddress.value = ""
            mBinding.editServiceZipCode.value = ""
        }

        mBinding.btnNext.onClick {
            hideKeyboard()
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

    private fun setValueInViewModel() {
        when (mViewModel.planType) {
            Plan.GASFUEL.value -> {
                mViewModel.serviceDetail.apply {
                    billingFirstName = mBinding.editBillingFirstName.value
                    billingMiddleInitial = mBinding.editBillingMiddleName.value
                    billingLastName = mBinding.editBillingLastName.value
                    billingAddress = mBinding.editBillingAddress.value
                    billingZip = mBinding.editZipCode.value
                    isTheBillingAddressTheSameAsTheServiceAddress = if (mBinding.checkBoxYes.isChecked) mBinding.checkBoxYes.text.toString() else mBinding.checkBoxNo.text.toString()
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
                mViewModel.serviceDetail.apply {
                    gasBillingAddress = mBinding.editBillingAddress.value
                    gasBillingAddress2 = ""
                    gasServiceAddress = mBinding.editServiceAddress.value
                    gasServiceAddress2 = ""
                    gasBillingFirstName = mBinding.editBillingFirstName.value
                    gasBillingMiddleInitial = mBinding.editBillingMiddleName.value
                    gasBillingLastName = mBinding.editBillingLastName.value
                    gasBillingCity = "Amherst"
                    gasBillingState = "MA"
                    gasBillingZip = mBinding.editZipCode.value
                    gasServiceZip = mBinding.editServiceZipCode.value
                    gasServiceCity = "Amherst"
                    gasServiceState = "MA"
                    isTheBillingAddressTheSameAsTheServiceAddress = if (mBinding.checkBoxYes.isChecked) mBinding.checkBoxYes.text.toString() else mBinding.checkBoxNo.text.toString()
                    gasAccountNumber = mBinding.editAccountNumber.value
                }
            }
        }
    }


}
