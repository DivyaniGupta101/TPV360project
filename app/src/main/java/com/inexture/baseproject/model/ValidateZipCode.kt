package com.inexture.baseproject.model

import com.google.gson.annotations.SerializedName

data class ValidateZipCode(
        @SerializedName("utid")
        var utid: Int?,
        @SerializedName("utilityname")
        var utilityname: String?,
        @SerializedName("market")
        var market: String?,
        @SerializedName("commodity")
        var commodity: String?,
        @SerializedName("fullname")
        var fullname: Any?

)