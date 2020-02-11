package com.tpv.android.model.network

import com.google.gson.annotations.SerializedName

data class OTPReq(@SerializedName("phonenumber") val phonenumber: String? = "")
data class OTPEmailReq(@SerializedName("email") val emailAddress: String? = "")

data class VerifyOTPReq(
        @SerializedName("otp")
        val otp: String?,
        @SerializedName("phonenumber")
        val phonenumber: String?
)

data class VerifyOTPEmailReq(
        @SerializedName("otp")
        val otp: String?,
        @SerializedName("email")
        val emailAddress: String?
)