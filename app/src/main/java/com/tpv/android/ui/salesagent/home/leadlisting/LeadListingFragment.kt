package com.tpv.android.ui.salesagent.home.leadlisting


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableArrayList
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.livinglifetechway.k4kotlin.core.hide
import com.livinglifetechway.k4kotlin.core.onClick
import com.ravikoradiya.liveadapter.LiveAdapter
import com.tpv.android.BR
import com.tpv.android.R
import com.tpv.android.databinding.BottomSheetBinding
import com.tpv.android.databinding.FragmentLeadListingBinding
import com.tpv.android.databinding.ItemBottomSheetBinding
import com.tpv.android.databinding.ItemLeadListBinding
import com.tpv.android.helper.OnBackPressCallBack
import com.tpv.android.helper.setPagination
import com.tpv.android.model.internal.BottomSheetItem
import com.tpv.android.model.network.LeadResp
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.utils.enums.LeadStatus
import com.tpv.android.utils.navigateSafe
import com.tpv.android.utils.setupToolbar

class LeadListingFragment : Fragment(), OnBackPressCallBack {

    lateinit var mBinding: FragmentLeadListingBinding
    lateinit var mViewModel: LeadListingViewModel
    var mListBottoSheet: ObservableArrayList<BottomSheetItem> = ObservableArrayList()

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

    override fun handleOnBackPressed(): Boolean {
        mViewModel.mLastSelectedStatus = ""
        return true
    }

    private fun initialize() {

        if (mViewModel.mLastSelectedStatus.isNullOrEmpty()) {
            mViewModel.mLastSelectedStatus = arguments?.let { LeadListingFragmentArgs.fromBundle(it) }?.item
        }

        mListBottoSheet.clear()
        mListBottoSheet.add(BottomSheetItem(getString(R.string.pending),
                LeadStatus.PENDING.value, false))
        mListBottoSheet.add(
                BottomSheetItem(getString(R.string.verified),
                        LeadStatus.VERIFIED.value, false))
        mListBottoSheet.add(
                BottomSheetItem(getString(R.string.declined),
                        LeadStatus.DECLINED.value, false))
        mListBottoSheet.add(
                BottomSheetItem(getString(R.string.disconnected),
                        LeadStatus.DISCONNECTED.value, false))
        mListBottoSheet.add(
                BottomSheetItem(getString(R.string.cancelled),
                        LeadStatus.CANCELLED.value, false)
        )
        mListBottoSheet?.add(
                BottomSheetItem(getString(R.string.expired),
                        LeadStatus.EXPIRED.value, false)
        )
        mListBottoSheet?.forEach {
            if (it.tag == mViewModel.mLastSelectedStatus) {
                it.isSelected = true
            }
        }

        setTitleAndRecyclerView(mViewModel.mLastSelectedStatus)

        mBinding.bottomStatusContainer.onClick {
            setBottomSheetDialog()
        }
    }

    private fun setBottomSheetDialog() {

        val binding = DataBindingUtil.inflate<BottomSheetBinding>(layoutInflater, R.layout.bottom_sheet, null, false)

        binding.filterContainer.hide()
        context?.let {

            val dialog = BottomSheetDialog(it)
            dialog.setContentView(binding.root)
            binding.item = getString(R.string.leads_status)

            mListBottoSheet.forEach {

                val bindingBottomSheet = DataBindingUtil.inflate<ItemBottomSheetBinding>(layoutInflater,
                        R.layout.item_bottom_sheet,
                        binding.bottomSheetItemContainer,
                        true)
                bindingBottomSheet.item = it

                if (it.tag == mViewModel.mLastSelectedStatus) {
                    bindingBottomSheet.radioContainer.isChecked = true
                }

                bindingBottomSheet.radioContainer.onClick {
                    mListBottoSheet.forEach {
                        it?.isSelected = it.tag == bindingBottomSheet.item?.tag
                    }
                    setTitleAndRecyclerView(bindingBottomSheet.item?.tag)
                    dialog.dismiss()
                }

            }

            dialog.show()
        }
    }

    private fun setTitleAndRecyclerView(status: String?) {
        setUpToolbarTitle(status)
        setRecyclerView(status)
    }


    private fun setUpToolbarTitle(leadStatus: String?) {
        mViewModel.mLastSelectedStatus = leadStatus
        var toolBarTitle = ""

        when (leadStatus) {
            LeadStatus.PENDING.value -> {
                toolBarTitle = getString(R.string.pending_leads)
                mBinding.textStatus.text = getString(R.string.pending)
            }
            LeadStatus.VERIFIED.value -> {
                toolBarTitle = getString(R.string.verified_leads)
                mBinding.textStatus.text = getString(R.string.verified)
            }
            LeadStatus.DECLINED.value -> {
                toolBarTitle = getString(R.string.declined_leads)
                mBinding.textStatus.text = getString(R.string.declined)
            }
            LeadStatus.DISCONNECTED.value -> {
                toolBarTitle = getString(R.string.disconnected_calls)
                mBinding.textStatus.text = getString(R.string.disconnected)
            }
            LeadStatus.CANCELLED.value -> {
                toolBarTitle = getString(R.string.cancelled_leads)
                mBinding.textStatus.text = getString(R.string.cancelled)
            }
            LeadStatus.EXPIRED.value -> {
                toolBarTitle = getString(R.string.expired_leads)
                mBinding.textStatus.text = getString(R.string.expired)
            }
        }
        setupToolbar(mBinding.toolbar, toolBarTitle, showMenuIcon = false, showBackIcon = true, backIconClickListener = {
            mViewModel.mLastSelectedStatus = ""
        })
    }

    private fun setRecyclerView(leadStatus: String?) {
        mViewModel.clearList()
        mBinding.listLead.adapter = null
        mBinding.listLead.clearOnScrollListeners()

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
