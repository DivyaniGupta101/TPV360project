package com.tpv.android.model.network
import com.google.gson.annotations.SerializedName


data class VerifySignatureReq(
    @SerializedName("tmp_lead_id")
    val tmpLeadId: String?
)
data class VerifySignatureResponse(
    @SerializedName("is_signature_captured")
    val isVerificationSignature: Boolean?
)