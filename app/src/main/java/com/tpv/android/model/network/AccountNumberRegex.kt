package com.tpv.android.model.network

import com.google.gson.annotations.SerializedName

data class AccountNumberRegexResp(
        val field_id: Int,
        val regex: String,
        val regex_message: String,
        @SerializedName("placeholder")
        val placeHolder: String?,
        @SerializedName("label")
        val label: String?,
        @SerializedName("options")
        val option:  ArrayList<Option>?)

data class AccountNumberRegexRequest(
        @SerializedName("form_id")
        val formId: String?,
        @SerializedName("utility_id")
        val utilityId: String?
)