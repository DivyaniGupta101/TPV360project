package com.tpv.android.model.network

import com.google.gson.annotations.SerializedName

data class VelidateLeadsDetailResp(
        @SerializedName("lead_temp_id")
        var leadTempId: String?,
        @SerializedName("errors")
        val errors: List<LeadVelidationError>
)


data class LeadVelidationError(
        @SerializedName("title")
        val title: String? = "",
        @SerializedName("msg")
        val msg: String? = ""
)

data class ValidateLeadsDetailReq(
        @SerializedName("form_id")
        val formId: String? = "",
        @SerializedName("agent_lat")
        val agentLat: String? = "",
        @SerializedName("agent_lng")
        val agentLng: String? = "",
        @SerializedName("other")
        val other: OtherData?,
        @SerializedName("fields")
        val fields: List<DynamicFormResp>
)