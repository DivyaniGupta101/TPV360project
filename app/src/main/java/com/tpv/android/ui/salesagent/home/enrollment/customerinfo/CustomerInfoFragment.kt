package com.tpv.android.ui.salesagent.home.enrollment.customerinfo


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.livinglifetechway.k4kotlin.core.hide
import com.livinglifetechway.k4kotlin.core.onClick
import com.livinglifetechway.k4kotlin.core.orFalse
import com.livinglifetechway.k4kotlin.core.show
import com.tpv.android.R
import com.tpv.android.databinding.*
import com.tpv.android.model.network.DynamicFormResp
import com.tpv.android.ui.salesagent.home.enrollment.SetEnrollViewModel
import com.tpv.android.utils.AppConstant
import com.tpv.android.utils.BindingAdapter.addressCombineValues
import com.tpv.android.utils.enums.DynamicField
import com.tpv.android.utils.navigateSafe
import com.tpv.android.utils.setupToolbar

class CustomerInfoFragment : Fragment() {
    private lateinit var mBinding: FragmentCustomerInfoBinding
    private lateinit var mViewModel: SetEnrollViewModel
    private var mViewGroup: ViewGroup? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_customer_info, container, false)
        mBinding.lifecycleOwner = this
        activity?.let { mViewModel = ViewModelProviders.of(it).get(SetEnrollViewModel::class.java) }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    private fun initialize() {
        if (mViewModel.dynamicFormData.find { it.type == DynamicField.FULLNAME.type && it.meta?.isPrimary == true } != null) {
            mBinding.item = mViewModel.dynamicFormData.find { it.type == DynamicField.FULLNAME.type && it.meta?.isPrimary == true }
            mBinding.textNameValue.addTextChangedListener {
                if (mBinding.textNameValue.text.isNotEmpty()) {
                    mBinding.textCustomerName.show()
                    mBinding.textNameValue.show()
                }
            }
        }

        if (mViewModel.dynamicFormData.filter { it.type == DynamicField.BOTHADDRESS.type }.isNotEmpty()) {
            val bothAddress: List<DynamicFormResp> = mViewModel.dynamicFormData.filter { it.type == DynamicField.BOTHADDRESS.type }
            bothAddress.forEach {
                val binding = DataBindingUtil.inflate<LayoutCustomerInfoAddressBinding>(layoutInflater,
                        R.layout.layout_customer_info_address,
                        mBinding.addressContainer,
                        true)
                binding.textLabel.text = it.label
                binding.textServiceAddress.text = getString(R.string.service_address)
                binding.textBillingAddress.text = getString(R.string.billing_address)
                addressCombineValues(
                        textView = binding.textServiceAddressValue,
                        unit = it.values?.get(AppConstant.SERVICEUNIT)?.toString(),
                        state = it.values?.get(AppConstant.SERVICESTATE)?.toString(),
                        county = it?.values?.get(AppConstant.SERVICECOUNTY)?.toString(),
                        addressLine1 = it.values?.get(AppConstant.SERVICEADDRESS1)?.toString(),
                        addressLine2 = it.values?.get(AppConstant.SERVICEADDRESS2)?.toString(),
                        city = it.values?.get(AppConstant.SERVICECITY)?.toString(),
                        country = it.values?.get(AppConstant.SERVICECOUNTRY)?.toString(),
                        zipcode = it.values?.get(AppConstant.SERVICEZIPCODE)?.toString()
                )
                addressCombineValues(
                        textView = binding.textBillingAddressValue,
                        unit = it.values?.get(AppConstant.BILLINGUNIT)?.toString(),
                        state = it.values?.get(AppConstant.BILLINGSTATE)?.toString(),
                        addressLine1 = it.values?.get(AppConstant.BILLINGADDRESS1)?.toString(),
                        addressLine2 = it.values?.get(AppConstant.BILLINGADDRESS2)?.toString(),
                        city = it.values?.get(AppConstant.BILLINGCITY)?.toString(),
                        county = it.values?.get(AppConstant.BILLINGCOUNTY)?.toString(),
                        country = it.values?.get(AppConstant.BILLINGCOUNTRY)?.toString(),
                        zipcode = it.values?.get(AppConstant.BILLINGZIPCODE)?.toString()
                )
                binding.textLabel.show()
                binding.textServiceAddress.show()
                binding.textServiceAddressValue.show()
                binding.textBillingAddress.show()
                binding.textBillingAddressValue.show()
            }

        }
        if (mViewModel.dynamicFormData.filter { it.type == DynamicField.ADDRESS.type }.isNotEmpty()) {
            val address: List<DynamicFormResp> = mViewModel.dynamicFormData.filter { it.type == DynamicField.ADDRESS.type }
            address.forEach {
                val binding = DataBindingUtil.inflate<LayoutCustomerInfoAddressBinding>(layoutInflater,
                        R.layout.layout_customer_info_address,
                        mBinding.addressContainer,
                        true)
                binding.textLabel.text = it.label
//                binding.textServiceAddress.text = getString(R.string.service_address) + " (${it.label})"
                addressCombineValues(
                        textView = binding.textServiceAddressValue,
                        unit = it.values?.get(AppConstant.UNIT)?.toString(),
                        state = it.values?.get(AppConstant.STATE)?.toString(),
                        addressLine1 = it.values?.get(AppConstant.ADDRESS1)?.toString(),
                        addressLine2 = it.values?.get(AppConstant.ADDRESS2)?.toString(),
                        city = it.values?.get(AppConstant.CITY)?.toString(),
                        county = it.values?.get(AppConstant.COUNTY)?.toString(),
                        country = it.values?.get(AppConstant.COUNTRY)?.toString(),
                        zipcode = it.values?.get(AppConstant.ZIPCODE)?.toString()
                )
                binding.textLabel.show()
                binding.textServiceAddressValue.show()
            }
        }

        if (mViewModel.dynamicFormData.find { it.type == DynamicField.TEXTBOX.type && it.meta?.isPrimary == true } != null) {
            mBinding.accountNumber = mViewModel.dynamicFormData.find { it.type == DynamicField.TEXTBOX.type && it.meta?.isPrimary == true }
            mBinding.textAccountNumberValue.addTextChangedListener {
                if (mBinding.textAccountNumberValue.text.isNotEmpty()) {
                    mBinding.textAccountNumberValue.show()
                    mBinding.textAccountNumber.show()
                }
            }
        }
        setupToolbar(mBinding.toolbar, getString(R.string.verify_customer_information), showBackIcon = true)

        setProgramInformation()

        mBinding.btnNext.onClick {
            if (mViewModel.dynamicSettings?.isEnableRecording.orFalse()) {
                Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_clientInfoFragment_to_recordingFragment)
            } else  if (mViewModel.dynamicSettings?.isEnableImageUpload.orFalse()) {
                Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_clientInfoFragment_to_uploadbillimageFragment)
            } else {
                Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_clientInfoFragment_to_signatureVerificationFragment)
            }
        }

    }

    private fun setProgramInformation() {
        mViewModel.programList.forEach { programsResp ->


            val titleBinding = DataBindingUtil.inflate<ItemTitleProgramsBinding>(layoutInflater, R.layout.item_title_programs, mBinding.infoContainer, true)
            titleBinding.item = mViewModel.selectedUtilityList.find { it.utid.toString() == programsResp.utilityId }?.commodity

            val binding = DataBindingUtil.inflate<ItemProgramsBinding>(layoutInflater, R.layout.item_programs, mBinding.infoContainer, true)
            binding.mainContainer.background = context?.getDrawable(R.drawable.bg_rectangle_border)
            binding.item = programsResp
            binding.customFieldsContainer.removeAllViews()
            binding.item?.costomFields?.forEachIndexed { index, programCustomField ->

                val bindingProgramCustomField = DataBindingUtil.inflate<LayoutProgramCustomFieldBinding>(layoutInflater,
                        R.layout.layout_program_custom_field,
                        binding.customFieldsContainer,
                        true)
                bindingProgramCustomField.item = programCustomField
                if (index == 0) {
                    bindingProgramCustomField.dividerView.show()
                } else {
                    bindingProgramCustomField.dividerView.hide()
                }
            }
        }
    }
}
