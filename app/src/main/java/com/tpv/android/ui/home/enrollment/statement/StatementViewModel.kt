package com.tpv.android.ui.home.enrollment.statement

import com.tpv.android.data.AppRepository
import com.tpv.android.model.ContractReq
import com.tpv.android.network.resources.CoroutineScopedViewModel

class StatementViewModel : CoroutineScopedViewModel() {

    fun saveContract(contractReq: ContractReq) = with(AppRepository) {
        saveContractCall(contractReq)
    }
}