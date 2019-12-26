package com.tpv.android.model.network

import com.google.gson.annotations.SerializedName

data class CommodityResp(
        @SerializedName("commodities")
        val commodities: List<Commodity>,
        @SerializedName("formname")
        val formname: String?,
        @SerializedName("id")
        val id: Int?
)

data class Commodity(
        @SerializedName("id")
        val id: String?,
        @SerializedName("name")
        val name: String?
)
