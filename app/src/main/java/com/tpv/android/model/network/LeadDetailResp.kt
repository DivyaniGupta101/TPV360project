package com.tpv.android.model.network


import com.google.gson.annotations.SerializedName

data class LeadDetailResp(
        @SerializedName("dispositions")
        val dispositions: List<String>?,
        @SerializedName("leadDeatils")
        val leadDeatils: List<DynamicFormResp>?,
        @SerializedName("programsDeatils")
        val programsDetails: List<ProgramsDetail>?,
        @SerializedName("verificationCode")
        val verificationCode: String
)