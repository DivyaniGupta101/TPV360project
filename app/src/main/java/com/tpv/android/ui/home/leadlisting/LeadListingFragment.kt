package com.tpv.android.ui.leadlisting


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProviders
import com.ravikoradiya.liveadapter.LiveAdapter
import com.tpv.android.BR
import com.tpv.android.R
import com.tpv.android.databinding.FragmentLeadListingBinding
import com.tpv.android.databinding.ItemLeadListBinding
import com.tpv.android.helper.setPagination
import com.tpv.android.model.LeadResp
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.ui.home.leadlisting.LeadListingViewModel
import com.tpv.android.utils.LeadStatus
import com.tpv.android.utils.setupToolbar

/**
 * A simple [Fragment] subclass.
 */
class LeadListingFragment : Fragment() {
    lateinit var mBinding: FragmentLeadListingBinding
    var toolBarTitle = ""
    lateinit var mViewModel: LeadListingViewModel

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

        val leadStatus = arguments?.let { LeadListingFragmentArgs.fromBundle(it) }?.item

        when (leadStatus) {
            LeadStatus.PENDING.value -> {
                toolBarTitle = getString(R.string.pending_leads)
            }
            LeadStatus.VERIFIED.value -> {
                toolBarTitle = getString(R.string.verified_leads)
            }
            LeadStatus.DECLINED.value -> {
                toolBarTitle = getString(R.string.declined_leads)
            }
            LeadStatus.DISCONNECTED.value -> {
                toolBarTitle = getString(R.string.disconnected_calls)
            }
        }

        setupToolbar(mBinding.toolbar, toolBarTitle, showMenuIcon = false, showBackIcon = true)

        mBinding.paginatedLayout.errorHandler = AlertErrorHandler(mBinding.root)
        mBinding.paginatedLayout.resource = mViewModel.leadsPaginatedResourceLiveData as LiveData<Resource<Any, APIError>>
        mBinding.paginatedLayout.showEmptyView = mViewModel.showEmptyView

        LiveAdapter(mViewModel.leadsLiveData, BR.item)
                .map<LeadResp, ItemLeadListBinding>(R.layout.item_lead_list)
                .into(mBinding.listLead)

        mBinding.listLead.setPagination(mViewModel.leadsPaginatedResourceLiveData) { page ->
            mViewModel.getLeadList(leadStatus, page)
        }

    }


}
