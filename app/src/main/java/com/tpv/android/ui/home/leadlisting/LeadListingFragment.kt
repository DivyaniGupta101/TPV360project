package com.tpv.android.ui.leadlisting


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.livinglifetechway.k4kotlin.core.onClick
import com.ravikoradiya.liveadapter.LiveAdapter
import com.tpv.android.BR
import com.tpv.android.R
import com.tpv.android.databinding.BottomSheetBinding
import com.tpv.android.databinding.FragmentLeadListingBinding
import com.tpv.android.databinding.ItemLeadListBinding
import com.tpv.android.helper.setPagination
import com.tpv.android.model.network.LeadResp
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.ui.home.leadlisting.LeadListingViewModel
import com.tpv.android.utils.enums.LeadStatus
import com.tpv.android.utils.navigateSafe
import com.tpv.android.utils.setupToolbar

class LeadListingFragment : Fragment() {
    lateinit var mBinding: FragmentLeadListingBinding
    var toolBarTitle = ""
    lateinit var mViewModel: LeadListingViewModel
    var status = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_lead_listing, container, false)
        mBinding.lifecycleOwner = this
        mViewModel = ViewModelProviders.of(this).get(LeadListingViewModel::class.java)
        return mBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    private fun initialize() {
        val leadStatus = arguments?.let { LeadListingFragmentArgs.fromBundle(it) }?.item
        status = leadStatus.orEmpty()
        setUpToolbarTitle(leadStatus)
        setRecyclerView(leadStatus)

        mBinding.bottomStatusContainer.onClick {
            setBottomSheet(status)
        }
    }

    private fun setUpToolbarTitle(leadStatus: String?) {
        when (leadStatus) {
            LeadStatus.PENDING.value -> {
                toolBarTitle = getString(R.string.pending_leads)
                mBinding.textStatus.setText(getString(R.string.pending))
            }
            LeadStatus.VERIFIED.value -> {
                toolBarTitle = getString(R.string.verified_leads)
                mBinding.textStatus.setText(getString(R.string.verified))
            }
            LeadStatus.DECLINED.value -> {
                toolBarTitle = getString(R.string.declined_leads)
                mBinding.textStatus.setText(getString(R.string.declined))
            }
            LeadStatus.DISCONNECTED.value -> {
                toolBarTitle = getString(R.string.disconnected_calls)
                mBinding.textStatus.setText(getString(R.string.disconnected_calls))
            }
        }

        setupToolbar(mBinding.toolbar, toolBarTitle, showMenuIcon = false, showBackIcon = true)
    }

    private fun setBottomSheet(leadStatus: String?) {
        val binding = DataBindingUtil.inflate<BottomSheetBinding>(layoutInflater, R.layout.bottom_sheet, null, false)
        context?.let {
            val dialog = BottomSheetDialog(it)
            dialog.setContentView(binding.root)

            binding.radioGroup?.setOnCheckedChangeListener { group, checkedId ->
                val radioButton = binding.radioGroup.findViewById<RadioButton>(checkedId)
                if (radioButton.isChecked) {
                    mBinding.textStatus.setText(radioButton.text)
//                setUpToolbarTitle(radioButton.tag.toString())
//                setRecyclerView(radioButton.tag.toString())
                }
                dialog.dismiss()
            }

            when (status) {
                LeadStatus.PENDING.value -> {
                    binding.radioPending.isChecked = true
                    status = LeadStatus.PENDING.value
                    binding.radioPending.tag = LeadStatus.PENDING.value
                }
                LeadStatus.VERIFIED.value -> {
                    binding.radioVerified.isChecked = true
                    status = LeadStatus.VERIFIED.value
                    binding.radioVerified.tag = LeadStatus.VERIFIED.value
                }
                LeadStatus.DECLINED.value -> {
                    binding.radioDeclined.isChecked = true
                    status = LeadStatus.DECLINED.value
                    binding.radioDeclined.tag = LeadStatus.DECLINED.value
                }
                LeadStatus.DISCONNECTED.value -> {
                    binding.radioDisconnected.isChecked = true
                    status = LeadStatus.DISCONNECTED.value
                    binding.radioDisconnected.tag = LeadStatus.DISCONNECTED.value
                }
            }

            dialog.show()
        }
    }

    private fun setRecyclerView(leadStatus: String?) {
        mBinding.paginatedLayout.errorHandler = AlertErrorHandler(mBinding.root)
        mBinding.paginatedLayout.resource = mViewModel.leadsPaginatedResourceLiveData as LiveData<Resource<Any, APIError>>
        mBinding.paginatedLayout.showEmptyView = mViewModel.showEmptyView

        LiveAdapter(mViewModel.leadsLiveData, BR.item)
                .map<LeadResp, ItemLeadListBinding>(R.layout.item_lead_list) {
                    onClick {
                        val id = it.binding.item?.id
                        Navigation.findNavController(mBinding.root).navigateSafe(
                                LeadListingFragmentDirections.actionLeadListingFragmentToLeadDetailFragment(id.orEmpty()))
                    }
                }
                .into(mBinding.listLead)

        mBinding.listLead.setPagination(mViewModel.leadsPaginatedResourceLiveData) { page ->
            mViewModel.getLeadList(leadStatus, page)
        }
    }


}
