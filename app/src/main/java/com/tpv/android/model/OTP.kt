package com.tpv.android.model

import com.google.gson.annotations.SerializedName

data class OTPReq(@SerializedName("phonenumber") val phonenumber: String? = "")

data class VerifyOTPReq(
        @SerializedName("otp")
        val otp: String?,
        @SerializedName("phonenumber")
        val phonenumber: String?
)