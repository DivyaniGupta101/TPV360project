package com.tpv.android.model

import com.google.gson.annotations.SerializedName

data class LogInResp(
        @SerializedName("data")
        var data: com.tpv.android.model.User?,
        @SerializedName("token")
        var token: String?
)