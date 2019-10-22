package com.inexture.baseproject.model

import com.google.gson.annotations.SerializedName

data class Dashboard(
        @SerializedName("status")
        var status: String?,
        @SerializedName("value")
        var value: Int?

)