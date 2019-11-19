package com.tpv.android.ui.home.enrollment.form.electricdetailform


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
import com.tpv.android.databinding.FragmentElectricDetailFormBinding
import com.tpv.android.ui.home.enrollment.SetEnrollViewModel
import com.tpv.android.utils.Plan
import com.tpv.android.utils.navigateSafe
import com.tpv.android.utils.setupToolbar

/**
 * A simple [Fragment] subclass.
 */
class ElectricDetailFormFragment : Fragment() {
    private lateinit var mBinding: FragmentElectricDetailFormBinding
    private lateinit var mViewModel: SetEnrollViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_electric_detail_form, container, false)
        activity?.let { mViewModel = ViewModelProviders.of(it).get(SetEnrollViewModel::class.java) }
        mBinding.viewModel = mViewModel
        return mBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar(mBinding.toolbar, getString(R.string.customer_data), showBackIcon = true)

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
            Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_electricDetailFormFragment_to_clientInfoFragment)

        }
    }

    private fun setValueInViewModel() {
        when (mViewModel.planType) {
            Plan.ELECTRICFUEL.value -> {
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
