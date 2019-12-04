package com.tpv.android.model

import com.google.gson.annotations.SerializedName

data class DashBoard(
        @SerializedName("status")
        var status: String?,
        @SerializedName("value")
        var value: Int?
)