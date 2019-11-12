package com.tpv.android.model

import com.google.gson.annotations.SerializedName

data class DashboardResp(
        @SerializedName("status")
        var status: String?,
        @SerializedName("value")
        var value: Int?
)