package com.tpv.android.ui.client.ui.reports.reportslisting

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
import com.tpv.android.databinding.FragmentClientReportsListingBinding
import com.tpv.android.databinding.ItemClientReportsBinding
import com.tpv.android.helper.setPagination
import com.tpv.android.model.network.ClientReportReq
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.utils.setupToolbar

class ClientReportsListingFragment : Fragment() {

    lateinit var mBinding: FragmentClientReportsListingBinding
    lateinit var mViewModel: ClientReportsListingViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_client_reports_listing, container, false)
        mBinding.lifecycleOwner = this
        mViewModel = ViewModelProviders.of(this).get(ClientReportsListingViewModel::class.java)
        return mBinding.root
    }

    private fun initialize() {
        setupToolbar(mBinding.toolbar, getString(R.string.reports), showMenuIcon = true,
                showBackIcon = true
        )
        setRecyclerView()
    }

    private fun setRecyclerView() {
        mViewModel.clearList()
        mBinding.listReports.adapter = null
        mBinding.listReports.clearOnScrollListeners()

        mBinding.paginatedLayout.errorHandler = AlertErrorHandler(mBinding.root)
        mBinding.paginatedLayout.resource = mViewModel.criticalAlertReportsPaginatedResourceLiveData as LiveData<Resource<Any, APIError>>
        mBinding.paginatedLayout.showEmptyView = mViewModel.showEmptyView

        LiveAdapter(mViewModel.criticalAlertReportsLiveData, BR.item)
                .map<ClientReportReq, ItemClientReportsBinding>(R.layout.item_client_reports) {
//                    onClick {
//                        val id = it.binding.item?.id
//                        Navigation.findNavController(mBinding.root).navigateSafe(
//                                LeadListingFragmentDirections.actionLeadListingFragmentToLeadDetailFragment(id.orEmpty()))
//                    }
                }
                .into(mBinding.listReports)

        mBinding.listReports.setPagination(mViewModel.criticalAlertReportsPaginatedResourceLiveData) { page ->
            mViewModel.getReportsList(page)
        }
    }
}