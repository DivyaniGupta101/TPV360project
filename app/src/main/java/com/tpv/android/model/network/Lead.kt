package com.tpv.android.model.network

import com.google.gson.annotations.SerializedName

data class LeadResp(
        @SerializedName("id")
        var id: String?,
        @SerializedName("refrence_id")
        var refrenceId: String?,
        @SerializedName("create_time")
        var createTime: String?,
        @SerializedName("status")
        var status: String?,
        @SerializedName("utility")
        var utility: String?,
        @SerializedName("commodity")
        var commodity: String?,
        @SerializedName("city")
        var city: String?,
        @SerializedName("state")
        var state: String?,
        @SerializedName("disposition")
        var disposition: String?

)

data class LeadReq(
        @SerializedName("leadstatus")
        val leadstatus: String?,
        @SerializedName("page")
        val page: Int?
)