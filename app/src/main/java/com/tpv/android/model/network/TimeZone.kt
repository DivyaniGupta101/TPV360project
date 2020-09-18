package com.tpv.android.model.network

import com.google.gson.annotations.SerializedName

data class TimeZone(
        @SerializedName("timezone")
        val timezone: String?,
        @SerializedName("value")
        val value: String?,
//        @SerializedName("selected")
        var selected: Boolean?
)

data class TimeZoneReq(
        @SerializedName("timezone")
        val timezone: String
)