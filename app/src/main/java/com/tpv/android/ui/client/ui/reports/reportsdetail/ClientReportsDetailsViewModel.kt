package com.tpv.android.ui.client.ui.reports.reportsdetail

import com.tpv.android.data.AppRepository
import com.tpv.android.network.resources.CoroutineScopedViewModel

class ClientReportsDetailsViewModel : CoroutineScopedViewModel() {

    fun getClientTimeLine() = with(AppRepository)
    {
        getTimeLineCall()
    }

    fun getClientLeadDetail() = with(AppRepository)
    {
        getClientLeadDetailsCall()
    }
}