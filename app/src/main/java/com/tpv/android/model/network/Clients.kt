package com.tpv.android.model.network

import com.google.gson.annotations.SerializedName


data class ClientsResp(
        @SerializedName("id")
        val id: String?,
        @SerializedName("name")
        val name: String?
)