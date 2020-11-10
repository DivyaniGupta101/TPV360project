package com.tpv.android.model.network
import com.google.gson.annotations.SerializedName


data class SendSignatureLinkReq(
    @SerializedName("mode")
    val mode: String?,
    @SerializedName("tmp_lead_id")
    val tmpLeadId: String?
)