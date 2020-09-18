package com.tpv.android.ui.salesagent.home.enrollment.programs

import com.tpv.android.data.AppRepository
import com.tpv.android.model.network.AccountNumberRegexRequest
import com.tpv.android.model.network.ForceUpdateReq
import com.tpv.android.network.resources.CoroutineScopedViewModel


class ProgramListingViewModel : CoroutineScopedViewModel() {

    fun getAccountNumberRegex(accountNumberRegexRequest: AccountNumberRegexRequest) = with(AppRepository)
    {
        getAccountNumberRegexCall(accountNumberRegexRequest)
    }
}