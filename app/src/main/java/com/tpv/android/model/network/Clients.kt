package com.tpv.android.model.network
import com.google.gson.annotations.SerializedName


data class ClientsResp(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?
)