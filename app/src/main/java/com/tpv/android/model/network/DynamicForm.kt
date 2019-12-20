package com.tpv.android.model.network

import com.google.gson.annotations.SerializedName

data class DynamicFormResp(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("type")
        val type: String?,
        @SerializedName("label")
        val label: String?,
        @SerializedName("meta")
        val meta: Meta?,
        @SerializedName("validations")
        val validations: Validations?,
        @SerializedName("values")
        var values: LinkedHashMap<String, Any>,
        var address: String? = "",
        var serviceAddress: String? = "",
        var billingAddress: String? = "",
        var isAddressSame: Boolean = false

)

data class Meta(
        @SerializedName("options")
        var options: ArrayList<Option>?,
        @SerializedName("style_as_a_button")
        val styleAsAButton: Boolean?,
        @SerializedName("placeholder")
        val placeHolder: String?
)

data class Option(
        @SerializedName("option")
        var option: String?,
        @SerializedName("selected")
        var selected: Boolean?
)

data class Validations(
        @SerializedName("required")
        val required: Boolean?,
        @SerializedName("length")
        val length: Int?,
        @SerializedName("verify")
        val verify: Boolean?

)

data class DynamicFormReq(
        @SerializedName("clientid")
        val clientid: String?,
        @SerializedName("commodity")
        val commodity: String?,
        @SerializedName("programid")
        val programid: String?
)