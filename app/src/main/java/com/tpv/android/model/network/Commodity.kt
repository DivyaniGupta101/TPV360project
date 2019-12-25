package com.tpv.android.model.network
import com.google.gson.annotations.SerializedName


data class CommodityResp(
    @SerializedName("formname")
    val formname: String?,
    @SerializedName("id")
    val id: Int?
)