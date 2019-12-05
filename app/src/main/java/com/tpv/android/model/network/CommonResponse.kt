package com.tpv.android.model.network

import com.google.gson.annotations.SerializedName

data class CommonResponse<T>(
        @SerializedName("status")
        var status: String?,
        @SerializedName("message")
        var message: String?,
        @SerializedName("data")
        var data: T?,
        @SerializedName("token")
        var token: String?,
        @SerializedName("postalCodes")
        var postalCodes: T?

)