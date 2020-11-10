package com.tpv.android.model.network


import com.google.gson.annotations.SerializedName

data class EnrollWithStateReq(
        @SerializedName("form_id")
        val formId: String?
)

data class EnrollWithStateResp(
        @SerializedName("is_enable_enroll_by_state")
        val isEnableEnrollByState: Boolean?
)