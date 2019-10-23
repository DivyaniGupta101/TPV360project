package com.tpv.android.model

import com.google.gson.annotations.SerializedName

data class MyLead(
        @SerializedName("id")
        var id: Int?,
        @SerializedName("refrence_id")
        var refrenceId: String?,
        @SerializedName("create_time")
        var createTime: String?,
        @SerializedName("status")
        var status: String?,
        @SerializedName("disposition")
        var disposition: String?

)