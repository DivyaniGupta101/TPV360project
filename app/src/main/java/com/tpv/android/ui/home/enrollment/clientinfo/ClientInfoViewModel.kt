package com.tpv.android.ui.home.enrollment.clientinfo

import com.tpv.android.data.AppRepository
import com.tpv.android.model.SaveLeadsDetailReq
import com.tpv.android.network.resources.CoroutineScopedViewModel

class ClientInfoViewModel : CoroutineScopedViewModel() {

    fun saveLeadDetail(leadsDetailReq: SaveLeadsDetailReq) = with(AppRepository)
    {
        saveLeadDetailCall(leadsDetailReq)
    }
}
