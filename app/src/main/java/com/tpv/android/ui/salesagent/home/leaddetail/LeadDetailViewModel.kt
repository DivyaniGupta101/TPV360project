package com.tpv.android.ui.salesagent.home.leaddetail

import com.tpv.android.data.AppRepository
import com.tpv.android.network.resources.CoroutineScopedViewModel

class LeadDetailViewModel : CoroutineScopedViewModel() {
    fun getLeadDetail(leadId: String?) = with(AppRepository) {
        getLeadDetailCall(leadId)
    }

}