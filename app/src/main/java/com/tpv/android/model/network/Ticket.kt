package com.tpv.android.model.network

import com.google.gson.annotations.SerializedName


data class TicketReq(
        @SerializedName("description")
        val description: String?,
        @SerializedName("email")
        val email: String?,
        @SerializedName("priority")
        val priority: Int?,
        @SerializedName("status")
        val status: Int? = 2,
        @SerializedName("subject")
        val subject: String?
)