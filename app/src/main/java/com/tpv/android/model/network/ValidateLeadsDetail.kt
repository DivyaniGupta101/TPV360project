package com.tpv.android.model.network

import com.google.gson.annotations.SerializedName

data class ValidateLeadsDetailResp(
        @SerializedName("lead_temp_id")
        var leadTempId: String?,
        @SerializedName("errors")
        val errors: List<LeadValidationError>
)


data class LeadValidationError(
        @SerializedName("title")
        val title: String? = "",
        @SerializedName("msg")
        val msg: String? = ""
)