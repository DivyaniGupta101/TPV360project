package com.tpv.android.model

import com.google.gson.annotations.SerializedName

data class SuccessReq(
        @SerializedName("verification_mode")
        val verificationType: String?,
        @SerializedName("leadid")
        val leadId: String?)