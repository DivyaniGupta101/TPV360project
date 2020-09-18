package com.tpv.android.model.network

import com.google.gson.annotations.SerializedName

data class AccountNumberRequest(
        @SerializedName("form_id")
        val formId: String?,
        @SerializedName("utility_id")
        val utilityId: String?
)