package com.tpv.android.model.network

import com.google.gson.annotations.SerializedName

data class ClientTimeLineResp(
        @SerializedName("agent")
        val agent: String?,
        @SerializedName("created_at")
        val createdAt: String?,
        @SerializedName("id")
        val id: Int?,
        @SerializedName("lead_status")
        val leadStatus: String?,
        @SerializedName("reason")
        val reason: String?,
        @SerializedName("related_lead_ids")
        val relatedLeadIds: String?,
        @SerializedName("status")
        val status: String?,
        @SerializedName("user_type")
        val userType: Any?
)