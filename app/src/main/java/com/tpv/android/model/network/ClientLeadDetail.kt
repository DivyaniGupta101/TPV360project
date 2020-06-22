package com.tpv.android.model.network

import com.google.gson.annotations.SerializedName


data class ClientLeadDetailResp(
        @SerializedName("lead_data")
        val leadData: LeadData?,
        @SerializedName("lead_details")
        val leadDetails: List<DynamicFormResp>?,
        @SerializedName("programs")
        val programs: List<ProgramsResp>?,
        @SerializedName("sales_agent")
        val salesAgent: SalesAgent?
)

data class LeadData(
        @SerializedName("form_id")
        val formId: String?,
        @SerializedName("id")
        val id: String?,
        @SerializedName("last_updated_on")
        val lastUpdatedOn: String?,
        @SerializedName("lead_submission_on")
        val leadSubmissionOn: String?,
        @SerializedName("reference_id")
        val referenceId: String?,
        @SerializedName("status")
        val status: String?,
        @SerializedName("user_id")
        val userId: Int?
)

data class SalesAgent(
        @SerializedName("agent_type")
        val agentType: String?,
        @SerializedName("client_name")
        val clientName: String?,
        @SerializedName("email")
        val email: String?,
        @SerializedName("id")
        val id: String?,
        @SerializedName("phone_number")
        val phoneNumber: String?,
        @SerializedName("salesagent_name")
        val salesagentName: String?,
        @SerializedName("salescenter_location_name")
        val salescenterLocationName: String?,
        @SerializedName("salescenter_name")
        val salescenterName: String?
)
