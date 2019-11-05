package com.tpv.android.ui.home.leadlisting

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.tpv.android.data.AppRepository
import com.tpv.android.helper.asLiveData
import com.tpv.android.model.LeadReq
import com.tpv.android.model.LeadResp
import com.tpv.android.network.resources.APIError
import com.tpv.android.network.resources.CoroutineScopedViewModel
import com.tpv.android.network.resources.PaginatedResource
import com.tpv.android.network.resources.paginatedDataCall

class LeadListingViewModel : CoroutineScopedViewModel() {

    private val leadsPaginatedResourceMutableLiveData = MutableLiveData<PaginatedResource<LeadResp, APIError>>()
    val leadsPaginatedResourceLiveData = leadsPaginatedResourceMutableLiveData.asLiveData()
    val leadsLiveData = Transformations.map(leadsPaginatedResourceLiveData) { it.data }


    init {
        getLeadList()
    }

    fun getLeadList() {
        paginatedDataCall(leadsPaginatedResourceMutableLiveData) { pageNumber ->
            AppRepository.getLeads(LeadReq("pending", pageNumber.toString()))
        }
    }


}