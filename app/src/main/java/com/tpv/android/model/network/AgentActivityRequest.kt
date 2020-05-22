package com.tpv.android.model.network


import com.google.gson.annotations.SerializedName

data class AgentActivityRequest(
        @SerializedName("activity_type")
        val activityType: String?,
        @SerializedName("lat")
        val lat: String?,
        @SerializedName("lng")
        val lng: String?
)