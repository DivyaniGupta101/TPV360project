package com.tpv.android.model.network


import com.google.gson.annotations.SerializedName

data class ForceUpdateReq(
        @SerializedName("app_version")
        val appVersion: Int?,
        @SerializedName("platform")
        val platform: String? = "android"
)