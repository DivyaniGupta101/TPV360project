package com.inexture.baseproject.model

import com.google.gson.annotations.SerializedName

data class LogInResp(
        @SerializedName("data")
        var data: User?,
        @SerializedName("token")
        var token: String?
)