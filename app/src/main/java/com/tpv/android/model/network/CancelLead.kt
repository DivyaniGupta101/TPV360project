package com.tpv.android.model.network

import com.google.gson.annotations.SerializedName


data class CancelLeadReq(
        @SerializedName("source")
        val source: String?
)