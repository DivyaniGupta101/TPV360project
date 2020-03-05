package com.tpv.android.ui.home.enrollment.clientinfo


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.livinglifetechway.k4kotlin.core.onClick
import com.livinglifetechway.k4kotlin.core.show
import com.tpv.android.R
import com.tpv.android.databinding.FragmentClientInfoBinding
import com.tpv.android.databinding.ItemProgramsBinding
import com.tpv.android.databinding.ItemTitleProgramsBinding
import com.tpv.android.ui.home.enrollment.SetEnrollViewModel
import com.tpv.android.utils.enums.DynamicField
import com.tpv.android.utils.navigateSafe
import com.tpv.android.utils.setupToolbar

class ClientInfoFragment : Fragment() {
    private lateinit var mBinding: FragmentClientInfoBinding
    private lateinit var mViewModel: SetEnrollViewModel
    private var mViewGroup: ViewGroup? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_client_info, container, false)
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
            mBinding.textCustomerName.show()
            mBinding.item = mViewModel.dynamicFormData.find { it.type == DynamicField.FULLNAME.type && it.meta?.isPrimary == true }
        }

        if (mViewModel.dynamicFormData.find { it.type == DynamicField.BOTHADDRESS.type && it.meta?.isPrimary == true } != null) {
            mBinding.textServiceAddress.show()
            mBinding.textBillingAddress.show()
            mBinding.address = mViewModel.dynamicFormData.find { it.type == DynamicField.BOTHADDRESS.type && it.meta?.isPrimary == true }
        }

        if (mViewModel.dynamicFormData.find { it.type == DynamicField.TEXTAREA.type && it.meta?.isPrimary == true } != null) {
            mBinding.textAccountNumber.show()
            mBinding.accountNumber = mViewModel.dynamicFormData.find { it.type == DynamicField.TEXTAREA.type && it.meta?.isPrimary == true }
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

        }
    }
}
