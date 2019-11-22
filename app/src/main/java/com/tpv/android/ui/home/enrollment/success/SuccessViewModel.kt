package com.tpv.android.ui.home.enrollment.success

import com.tpv.android.data.AppRepository
import com.tpv.android.model.SuccessReq
import com.tpv.android.network.resources.CoroutineScopedViewModel

class SuccessViewModel : CoroutineScopedViewModel() {

    fun selfVerification(successReq: SuccessReq) = with(AppRepository) {
        selfVerificationCall(successReq)
    }
}