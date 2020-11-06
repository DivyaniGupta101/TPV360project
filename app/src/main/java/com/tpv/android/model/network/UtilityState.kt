package com.tpv.android.model.network


import com.google.gson.annotations.SerializedName

data class UtilityStateResp(
    @SerializedName("id")
    val id: String?,
    @SerializedName("state")
    val state: String?
)