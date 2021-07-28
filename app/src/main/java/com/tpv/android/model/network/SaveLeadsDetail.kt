package com.tpv.android.model.network

import com.google.gson.annotations.SerializedName

data class SaveLeadsDetailResp(
        @SerializedName("id")
        var id: String?,
        @SerializedName("reference_id")
        var referenceId: String?,
        @SerializedName("phone_number")
        var phoneNumber: String?,
        @SerializedName("is_on_customer_call_number")
        var isOnCustomerCallNumber: Boolean? ,
        @SerializedName("is_on_self_tpv")
        var isOnSelfTPV: Boolean?,
        @SerializedName("is_on_outbound_tpv")
        var isOnOutBoundTPV: Boolean?
)

data class SaveLeadsDetailReq(
        @SerializedName("lead_tmp_id")
        var leadTempId: String?,
        @SerializedName("agent_ipaddress")
        var agent_ipaddress : String?,
        @SerializedName("form_id")
        val formId: String? = "",
        @SerializedName("other")
        val other: OtherData?,
        @SerializedName("is_billing_image_available")
        val billingimage: Boolean?,
        @SerializedName("fields")
        val fields: List<DynamicFormResp>
)

data class OtherData(
        @SerializedName("program_id")
        val programId: String? = "",
        @SerializedName("zipcode")
        val zipcode: String? = "",
        @SerializedName("enrollment_using")
        val enrollmentUsing: String? = ""
)