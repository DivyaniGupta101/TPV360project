package com.tpv.android.ui.salesagent.home.enrollment.signatureVerification

import com.tpv.android.data.AppRepository
import com.tpv.android.model.network.CancelLeadReq
import com.tpv.android.model.network.SendSignatureLinkReq
import com.tpv.android.model.network.VerifySignatureReq
import com.tpv.android.network.resources.CoroutineScopedViewModel

class SignatureVerificationViewModel : CoroutineScopedViewModel() {
    fun sendSignature(sendSignatureLinkReq: SendSignatureLinkReq) = with(AppRepository)
    {
        sendSignatureCall(sendSignatureLinkReq)
    }

    fun cancelEnrollLead(tempId: String, cancelLeadReq: CancelLeadReq) = with(AppRepository)
    {
        cancelEnrollLeadCall(tempId, cancelLeadReq)
    }

    fun verifySignature(verifySignatureReq: VerifySignatureReq) = with(AppRepository)
    {
        verifySignatureCall(verifySignatureReq)
    }
}