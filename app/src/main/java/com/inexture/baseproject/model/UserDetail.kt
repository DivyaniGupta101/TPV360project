package com.inexture.baseproject.model

import com.google.gson.annotations.SerializedName

data class UserDetail(
        @SerializedName("id")
        var id: Int?,
        @SerializedName("first_name")
        var firstName: String?,
        @SerializedName("last_name")
        var lastName: String?,
        @SerializedName("email")
        var email: String?,
        @SerializedName("userid")
        var userid: String?,
        @SerializedName("title")
        var title: Any?,
        @SerializedName("verification_code")
        var verificationCode: String?,
        @SerializedName("session_id")
        var sessionId: String?,
        @SerializedName("created_at")
        var createdAt: String?,
        @SerializedName("updated_at")
        var updatedAt: String?,
        @SerializedName("parent_id")
        var parentId: Int?,
        @SerializedName("client_id")
        var clientId: Int?,
        @SerializedName("salescenter_id")
        var salescenterId: Int?,
        @SerializedName("access_level")
        var accessLevel: String?,
        @SerializedName("status")
        var status: String?,
        @SerializedName("location_id")
        var locationId: Int?,
        @SerializedName("last_activity")
        var lastActivity: String?,
        @SerializedName("deactivationreason")
        var deactivationreason: String?,
        @SerializedName("hire_options")
        var hireOptions: String?

)