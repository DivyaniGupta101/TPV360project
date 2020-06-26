package com.tpv.android.model.network

import com.google.gson.annotations.SerializedName


data class ClientReportResp(
        @SerializedName("alert_status")
        val alertStatus: String?,
        @SerializedName("client_name")
        val clientName: String?,
        @SerializedName("date_of_submission")
        val dateOfSubmission: String?,
        @SerializedName("date_of_tpv")
        val dateOfTpv: String?,
        @SerializedName("lead_id")
        val leadId: String?,
        @SerializedName("lead_status")
        val leadStatus: String?,
        @SerializedName("reference_id")
        val referenceId: String?,
        @SerializedName("salesagent_name")
        val salesagentName: String?,
        @SerializedName("salesceneter_location_address")
        val salesceneterLocationAddress: String?,
        @SerializedName("salescenter_location_name")
        val salescenterLocationName: String?,
        @SerializedName("salescenter_name")
        val salescenterName: String?
)

data class ClientReportReq(
        @SerializedName("client_id")
        var clientId: String? = "",
        @SerializedName("from_date")
        var fromDate: String?,
        @SerializedName("page")
        var page: Int? = 0,
        @SerializedName("salescenter_id")
        var salescenterId: String? = "",
        @SerializedName("search_text")
        var searchText: String? = "",
        @SerializedName("sort_by")
        var sortBy: String?,
        @SerializedName("sort_order")
        var sortOrder: String?,
        @SerializedName("to_date")
        var toDate: String?,
        @SerializedName("verification_from_date")
        var verificationFromDate: String? = "",
        @SerializedName("verification_to_date")
        var verificationToDate: String? = ""
)