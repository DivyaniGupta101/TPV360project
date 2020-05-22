package com.tpv.android.model.network

import com.google.gson.annotations.SerializedName

data class AgentLocationRequest(
        @SerializedName("lat")
        val lat: String?,
        @SerializedName("lng")
        val lng: String?
)