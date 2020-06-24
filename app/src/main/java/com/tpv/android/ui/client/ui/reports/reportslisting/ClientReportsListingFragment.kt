package com.tpv.android.ui.client.ui.reports.reportslisting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableArrayList
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.livinglifetechway.k4kotlin.core.onClick
import com.livinglifetechway.k4kotlin.core.setItems
import com.ravikoradiya.liveadapter.LiveAdapter
import com.tpv.android.BR
import com.tpv.android.R
import com.tpv.android.databinding.BottomSheetBinding
import com.tpv.android.databinding.FragmentClientReportsListingBinding
import com.tpv.android.databinding.ItemBottomSheetBinding
import com.tpv.android.databinding.ItemClientReportsBinding
import com.tpv.android.helper.setPagination
import com.tpv.android.model.internal.BottomSheetItem
import com.tpv.android.model.network.ClientReportResp
import com.tpv.android.model.network.ClientsResp
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.utils.enums.ClientMenuItem
import com.tpv.android.utils.navigateSafe
import com.tpv.android.utils.setItemSelection
import com.tpv.android.utils.setupToolbar

class ClientReportsListingFragment : Fragment() {

    lateinit var mBinding: FragmentClientReportsListingBinding
    lateinit var mViewModel: ClientReportsListingViewModel
    var mClientList: ArrayList<ClientsResp> = ArrayList()
    var mSalesCenterList: ArrayList<ClientsResp> = ArrayList()
    var mListBottoSheet: ObservableArrayList<BottomSheetItem> = ObservableArrayList()
    var mLastSelectedSortBy = "lead id ascending"
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

        mListBottoSheet.add(BottomSheetItem("Lead Id Ascending", "lead id ascending", false))
        mListBottoSheet.add(BottomSheetItem("Lead Id Descending", "lead id descending", false))
        mListBottoSheet.add(BottomSheetItem("Reference Id Ascending", "reference id ascending", false))
        mListBottoSheet.add(BottomSheetItem("Reference Id Descending", "reference id descending", false))
        mListBottoSheet.add(BottomSheetItem("Alert Status Ascending", "alert status ascending", false))
        mListBottoSheet.add(BottomSheetItem("Alert Status Descending", "alert status descending", false))
        mListBottoSheet.add(BottomSheetItem("Lead Status Ascending", "lead status ascending", false))
        mListBottoSheet.add(BottomSheetItem("Lead Status Descending", "lead status descending", false))
        mListBottoSheet.add(BottomSheetItem("Client Name Ascending", "client name ascending", false))
        mListBottoSheet.add(BottomSheetItem("Client Name Descending", "client name descending", false))
        mListBottoSheet.add(BottomSheetItem("Salescenter Name Ascending", "salescenter name ascending", false))
        mListBottoSheet.add(BottomSheetItem("Salescenter Name Descending", "salescenter name descending", false))
        mListBottoSheet.add(BottomSheetItem("Salesceneter Location Address Ascending", "salesceneter location address ascending", false))
        mListBottoSheet.add(BottomSheetItem("Salesceneter Location Address Descending", "salesceneter location address descending", false))
        mListBottoSheet.add(BottomSheetItem("Salesagent Name Ascending", "salesagent name ascending", false))
        mListBottoSheet.add(BottomSheetItem("Salesagent Name Descending", "salesagent name descending", false))
        mListBottoSheet.add(BottomSheetItem("Date Of Submission Ascending", "date of submission ascending", false))
        mListBottoSheet.add(BottomSheetItem("Date Of Submission Descending", "date of submission descending", false))
        mListBottoSheet.add(BottomSheetItem("Date Of Tpv Ascending", "date of tpv ascending", false))
        mListBottoSheet.add(BottomSheetItem("Date Of Tpv Descending", "date of tpv descending", false))
        mListBottoSheet.add(BottomSheetItem("Salescenter Location Name Ascending", "salescenter location name ascending", false))
        mListBottoSheet.add(BottomSheetItem("Salescenter Location Name Descending", "salescenter location name descending", false))

        mListBottoSheet.forEach {
            if (mLastSelectedSortBy == it.tag) {
                it.isSelected = true
            }
        }


        val liveData = mViewModel.getClients()
        liveData.observe(this, Observer {
            it?.ifSuccess { list ->
                mBinding.layoutSpinnerAllClients.textTitle.text = getString(R.string.clients)

                mClientList.addAll(list.orEmpty())

                val spinnerValueList = arrayListOf("All Clients")
                spinnerValueList.addAll(list?.map { it.name.orEmpty() }.orEmpty())
                mBinding.layoutSpinnerAllClients.spinner.setItems(spinnerValueList as ArrayList<String>?)

                getSalesCenteres()
            }
        })
        mBinding.resource = liveData as LiveData<Resource<Any, APIError>>


        mBinding.sortByContainer.onClick {
            handleBottomSheet()
        }
    }

    private fun handleBottomSheet() {
        val binding = DataBindingUtil.inflate<BottomSheetBinding>(layoutInflater, R.layout.bottom_sheet, null, false)

        context?.let {

            val dialog = BottomSheetDialog(it)
            dialog.setContentView(binding.root)
            binding.item = getString(R.string.sort_by)

            mListBottoSheet.forEach {

                val bindingBottomSheet = DataBindingUtil.inflate<ItemBottomSheetBinding>(layoutInflater,
                        R.layout.item_bottom_sheet,
                        binding.bottomSheetItemContainer,
                        true)

                bindingBottomSheet.item = it
//
                if (it.tag == mLastSelectedSortBy) {
                    bindingBottomSheet.radioContainer.isChecked = true
                }

                bindingBottomSheet.radioContainer.onClick {
                    mLastSelectedSortBy = bindingBottomSheet.item?.tag.toString()
                    mListBottoSheet.forEach {
                        it?.isSelected = it.tag == bindingBottomSheet.item?.tag
                    }
//                    setTitleAndRecyclerView(bindingBottomSheet.item?.tag)
                    dialog.dismiss()
                }

            }

            dialog.show()
        }
    }

    private fun getSalesCenteres() {
        val liveData = mViewModel.getSalesCenter()
        liveData.observe(this, Observer {
            it?.ifSuccess { list ->
                mBinding.layoutSpinnerSalesCenter.textTitle.text = getString(R.string.sales_centers)

                mSalesCenterList.addAll(list.orEmpty())

                val spinnerValueList = arrayListOf("All Sales Centers")
                spinnerValueList.addAll(list?.map { it.name.orEmpty() }.orEmpty())
                mBinding.layoutSpinnerSalesCenter.spinner.setItems(spinnerValueList as ArrayList<String>?)

                setRecyclerView()
            }
        })

        mBinding.resource = liveData as LiveData<Resource<Any, APIError>>
    }

    private fun setRecyclerView() {
        mViewModel.clearList()
        mBinding.listReports.adapter = null
        mBinding.listReports.clearOnScrollListeners()

        mBinding.paginatedLayout.errorHandler = AlertErrorHandler(mBinding.root)
        mBinding.paginatedLayout.resource = mViewModel.criticalAlertReportsPaginatedResourceLiveData as LiveData<Resource<Any, APIError>>
        mBinding.paginatedLayout.showEmptyView = mViewModel.showEmptyView

        LiveAdapter(mViewModel.criticalAlertReportsLiveData, BR.item)
                .map<ClientReportResp, ItemClientReportsBinding>(R.layout.item_client_reports) {
                    onClick {
//                        val id = it.binding.item?.id
                        Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_clientReportsListingFragment_to_clientReportsDetailsFragment)
                    }
                }
                .into(mBinding.listReports)

        mBinding.listReports.setPagination(mViewModel.criticalAlertReportsPaginatedResourceLiveData) { page ->
            mViewModel.getReportsList(page)
        }
    }

    override fun onResume() {
        super.onResume()
        setItemSelection(ClientMenuItem.REPORTS.value)
    }
}