package com.tpv.android.model.network

import com.google.gson.annotations.SerializedName

data class SaveLeadsDetailResp(
        @SerializedName("id")
        var id: String?,
        @SerializedName("reference_id")
        var referenceId: String?,
        @SerializedName("phone_number")
        var phoneNumber: String?
)

data class SaveLeadsDetailReq(
        @SerializedName("lead_temp_id")
        var leadTempId: String?,
        @SerializedName("form_id")
        val formId: String? = "",
        @SerializedName("other")
        val other: OtherData?,
        @SerializedName("fields")
        val fields: List<DynamicFormResp>
)

data class OtherData(
        @SerializedName("program_id")
        val programId: String? = "",
        @SerializedName("zipcode")
        val zipcode: String? = ""
)