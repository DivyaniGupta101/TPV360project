package com.tpv.android.model

import com.google.gson.annotations.SerializedName

data class LoginReq(
        @SerializedName("email") val email: String?,
        @SerializedName("password") val password: String?)

data class LoginResp(
        @SerializedName("first_name")
        var firstName: String? = null,
        @SerializedName("last_name")
        var lastName: String? = null,
        @SerializedName("email")
        var email: String? = null,
        @SerializedName("userid")
        var userid: String? = null,
        @SerializedName("client_id")
        var clientId: Int? = null

)