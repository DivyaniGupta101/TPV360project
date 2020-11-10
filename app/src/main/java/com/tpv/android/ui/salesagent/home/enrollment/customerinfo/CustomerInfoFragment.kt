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
import com.livinglifetechway.k4kotlin.core.show
import com.tpv.android.R
import com.tpv.android.databinding.FragmentCustomerInfoBinding
import com.tpv.android.databinding.ItemProgramsBinding
import com.tpv.android.databinding.ItemTitleProgramsBinding
import com.tpv.android.databinding.LayoutProgramCustomFieldBinding
import com.tpv.android.ui.salesagent.home.enrollment.SetEnrollViewModel
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

        if (mViewModel.dynamicFormData.find { it.type == DynamicField.BOTHADDRESS.type && it.meta?.isPrimary == true } != null) {
            mBinding.address = mViewModel.dynamicFormData.find { it.type == DynamicField.BOTHADDRESS.type && it.meta?.isPrimary == true }
            mBinding.textServiceAddressValue.addTextChangedListener {
                if (mBinding.textServiceAddressValue.text.isNotEmpty()) {
                    mBinding.textServiceAddress.show()
                    mBinding.textServiceAddressValue.show()
                }
            }

            mBinding.textBillingAddressValue.addTextChangedListener {
                if (mBinding.textBillingAddressValue.text.isNotEmpty()) {
                    mBinding.textBillingAddress.show()
                    mBinding.textBillingAddressValue.show()
                }
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
            Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_clientInfoFragment_to_recordingFragment)
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
