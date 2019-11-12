package com.tpv.android.model

import com.google.gson.annotations.SerializedName

data class ZipCodeResp(
        @SerializedName("zipcode")
        var zipcode: String?,
        @SerializedName("label")
        var label: String?,
        @SerializedName("value")
        var value: String?
)

data class ZipCodeReq(
    @SerializedName("zipcode")
    val zipcode: String?
)