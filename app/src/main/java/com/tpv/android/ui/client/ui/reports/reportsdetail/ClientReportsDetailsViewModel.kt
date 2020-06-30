package com.tpv.android.ui.client.ui.reports.reportsdetail

import com.tpv.android.data.AppRepository
import com.tpv.android.network.resources.CoroutineScopedViewModel

class ClientReportsDetailsViewModel : CoroutineScopedViewModel() {

    fun getClientTimeLine(id:String) = with(AppRepository)
    {
        getTimeLineCall(id)
    }

    fun getClientLeadDetail(leadId:String) = with(AppRepository)
    {
        getClientLeadDetailsCall(leadId  )
    }
}