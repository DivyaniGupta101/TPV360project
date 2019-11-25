package com.tpv.android.ui.home.enrollment.form.personaldetails

import com.tpv.android.data.AppRepository
import com.tpv.android.model.OTPReq
import com.tpv.android.model.VerifyOTPReq
import com.tpv.android.network.resources.CoroutineScopedViewModel

class PersonalDetailViewModel : CoroutineScopedViewModel() {
    fun generateOTP(otpReq: OTPReq) = with(AppRepository) {
        generateOTPCall(otpReq)
    }

    fun verifyOTP(verifyOTPReq: VerifyOTPReq) = with(AppRepository) {
        verifyOTPCall(verifyOTPReq)
    }
}