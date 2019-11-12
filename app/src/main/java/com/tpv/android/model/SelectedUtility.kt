package com.tpv.android.model

import com.google.gson.annotations.SerializedName

data class SelectedUtilityResp(
    @SerializedName("commodity")
    val commodity: String?,
    @SerializedName("fullname")
    val fullname: String?,
    @SerializedName("market")
    val market: String?,
    @SerializedName("utid")
    val utid: Int?,
    @SerializedName("utilityname")
    val utilityname: String?
)

data class SelectedUtilityReq(
    @SerializedName("commodity")
    val commodity: String?,
    @SerializedName("zipcode")
    val zipcode: String?
)