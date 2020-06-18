package com.tpv.android.ui.client.ui.reports.reportslisting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.tpv.android.data.AppRepository
import com.tpv.android.helper.asLiveData
import com.tpv.android.model.network.ClientReportReq
import com.tpv.android.model.network.ClientReportResp
import com.tpv.android.model.network.ClientsResp
import com.tpv.android.network.resources.CoroutineScopedViewModel
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError

class ClientReportsListingViewModel : CoroutineScopedViewModel() {
    private val criticalAlertReportsPaginatedResourceMutableLiveData = MutableLiveData<Resource<List<ClientReportResp>, APIError>>()
    val criticalAlertReportsPaginatedResourceLiveData = criticalAlertReportsPaginatedResourceMutableLiveData.asLiveData()

    val criticalAlertReportsLiveData: LiveData<List<ClientReportResp>> = Transformations.map(criticalAlertReportsPaginatedResourceLiveData) { it.data }

    private val clientResourceMutableLiveData = MutableLiveData<Resource<List<ClientsResp>, APIError>>()
    val clientResourceLiveData = clientResourceMutableLiveData.asLiveData()

    val clientLiveData: LiveData<List<ClientsResp>> = Transformations.map(clientResourceLiveData) { it.data }


    val showEmptyView: LiveData<Boolean> = Transformations.map(criticalAlertReportsLiveData) {
        it?.isEmpty()
    }


    init {
        getClients()
    }

    fun getClients() = with(AppRepository) {
        getClientsCall().observeForever { clientResourceMutableLiveData.value = it }
    }

    fun getReportsList(page: Int? = 1) = with(AppRepository) {
        val leadstatus = ClientReportReq(
                clientId = 102,
                salescenterId = 2002,
                fromDate = "2020-05-05",
                toDate = "2020-06-05",
                verificationFromDate = "2020-05-05",
                verificationToDate = "2020-06-05",
                sortBy = "salesagent_name",
                sortOrder = "asc",
                page = page,
                searchText = "")

        leadstatus?.let {
            getCriticalAlertReportListCall(criticalAlertReportsLiveData.value.orEmpty(), it)
                    .observeForever { criticalAlertReportsPaginatedResourceMutableLiveData.value = it }
        }
    }

    fun clearList() {
        criticalAlertReportsPaginatedResourceMutableLiveData.value = Resource.empty()
    }
}