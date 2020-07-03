package com.tpv.android.ui.client.ui.reports.reportslisting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.tpv.android.data.AppRepository
import com.tpv.android.helper.asLiveData
import com.tpv.android.model.network.ClientReportReq
import com.tpv.android.model.network.ClientReportResp
import com.tpv.android.network.resources.CoroutineScopedViewModel
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError

class ClientReportsListingViewModel : CoroutineScopedViewModel() {
    private val criticalAlertReportsPaginatedResourceMutableLiveData = MutableLiveData<Resource<List<ClientReportResp>, APIError>>()
    val criticalAlertReportsPaginatedResourceLiveData = criticalAlertReportsPaginatedResourceMutableLiveData.asLiveData()

    val criticalAlertReportsLiveData: LiveData<List<ClientReportResp>> = Transformations.map(criticalAlertReportsPaginatedResourceLiveData) { it.data }

    var clientReportReq: ClientReportReq? = null
    val showEmptyView: LiveData<Boolean> = Transformations.map(criticalAlertReportsLiveData) {
        it?.isEmpty()
    }

    var mLastPosition: Int = 0

    fun getClients() = with(AppRepository) {
        getClientsCall()
    }

    fun getSalesCenter(clientId: String?) = with(AppRepository)
    {
        getSalesCenterCall(clientId)
    }

    fun getReportsList(page: Int? = 1) = with(AppRepository) {
        clientReportReq?.page = page

        clientReportReq?.let {
            getCriticalAlertReportListCall(criticalAlertReportsLiveData.value.orEmpty(), it)
                    .observeForever { criticalAlertReportsPaginatedResourceMutableLiveData.value = it }
        }
    }

    fun clearList() {
        criticalAlertReportsPaginatedResourceMutableLiveData.value = Resource.empty()
    }

}