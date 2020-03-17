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