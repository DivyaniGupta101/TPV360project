package com.tpv.android.ui.auth.forogtpassword

import com.tpv.android.data.AppRepository
import com.tpv.android.model.ForgotPasswordReq
import com.tpv.android.network.resources.CoroutineScopedViewModel

class ForgotPasswordViewModel : CoroutineScopedViewModel() {
    fun forgotPassword(forgotPasswordReq: ForgotPasswordReq) = with(AppRepository) {
        forgotPasswordCall(forgotPasswordReq)
    }
}