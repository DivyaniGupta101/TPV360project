package com.tpv.android.ui.home.leadlisting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.tpv.android.data.AppRepository
import com.tpv.android.helper.asLiveData
import com.tpv.android.model.LeadReq
import com.tpv.android.model.LeadResp
import com.tpv.android.network.resources.CoroutineScopedViewModel
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError

class LeadListingViewModel : CoroutineScopedViewModel() {

    private val leadsPaginatedResourceMutableLiveData = MutableLiveData<Resource<List<LeadResp>, APIError>>()
    val leadsPaginatedResourceLiveData = leadsPaginatedResourceMutableLiveData.asLiveData()

    val leadsLiveData: LiveData<List<LeadResp>> = Transformations.map(leadsPaginatedResourceLiveData) { it.data }

    val showEmptyView: LiveData<Boolean> = Transformations.map(leadsLiveData) {
        it.isEmpty()
    }


    fun getLeadList(leadstatus: String?, page: Int? = 1) = with(AppRepository) {
        getLeads(leadsLiveData.value.orEmpty(), LeadReq(leadstatus, page = page))
                .observeForever { leadsPaginatedResourceMutableLiveData.value = it }
    }
}