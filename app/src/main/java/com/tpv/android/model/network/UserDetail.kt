package com.tpv.android.model.network

import com.google.gson.annotations.SerializedName

data class UserDetail(
        @SerializedName("access_level")
        val accessLevel: String?,
        @SerializedName("agent_type")
        val agentType: Any?,
        @SerializedName("client_id")
        val clientId: String?,
        @SerializedName("client_name")
        val clientName: String?,
        @SerializedName("created_at")
        val createdAt: String?,
        @SerializedName("deactivationreason")
        val deactivationreason: Any?,
        @SerializedName("email")
        val email: String?,
        @SerializedName("first_name")
        val firstName: String?,
        @SerializedName("full_name")
        val fullName: String?,
        @SerializedName("hire_options")
        val hireOptions: Any?,
        @SerializedName("id")
        val id: Int?,
        @SerializedName("is_block")
        val isBlock: String?,
        @SerializedName("last_activity")
        val lastActivity: String?,
        @SerializedName("last_name")
        val lastName: String?,
        @SerializedName("location_id")
        val locationId: Int?,
        @SerializedName("parent_id")
        val parentId: Int?,
        @SerializedName("phone_no")
        val phoneNo: Any?,
        @SerializedName("profile_picture")
        val profilePicture: String?,
        @SerializedName("sales_center_name")
        val salesCenterName: String?,
        @SerializedName("salescenter_id")
        val salescenterId: String?,
        @SerializedName("session_id")
        val sessionId: String?,
        @SerializedName("status")
        val status: String?,
        @SerializedName("timezone")
        val timezone: String?,
        @SerializedName("title")
        val title: Any?,
        @SerializedName("updated_at")
        val updatedAt: String?,
        @SerializedName("userid")
        val userid: String?,
        @SerializedName("verification_code")
        val verificationCode: String? ,
        @SerializedName("dashboard_url")
        val dashBoardURL: String?
)