package com.tpv.android.model
import com.google.gson.annotations.SerializedName


data class ForgotPasswordReq(
    @SerializedName("email")
    val email: String?
)