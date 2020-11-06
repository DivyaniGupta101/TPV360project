package com.tpv.android.model.network

import com.google.gson.annotations.SerializedName

data class UtilityResp(
        @SerializedName("commodity")
        val commodity: String?,
        @SerializedName("fullname")
        val fullname: String?,
        @SerializedName("market")
        val market: String?,
        @SerializedName("utid")
        val utid: Int?,
        @SerializedName("commodity_id")
        val commodityId: String?,
        @SerializedName("utilityname")
        val utilityname: String?
)

data class UtilityReq(
        @SerializedName("commodity")
        val commodity: String?,
        @SerializedName("zipcode")
        val zipcode: String?,
        @SerializedName("state")
        val state: String?
)