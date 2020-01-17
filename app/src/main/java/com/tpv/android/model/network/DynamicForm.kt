package com.tpv.android.model.network

import com.google.gson.annotations.SerializedName

data class DynamicFormResp(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("type")
        val type: String?,
        @SerializedName("label")
        var label: String?,
        @SerializedName("meta")
        val meta: Meta?,
        @SerializedName("validations")
        val validations: Validations?,
        @SerializedName("values")
        var values: LinkedHashMap<String, Any>,
        var isAddressSame: Boolean = false,
        var leadDetailText: String? = ""
)

data class Meta(
        @SerializedName("options")
        var options: ArrayList<Option>?,
        @SerializedName("style_as_a_button")
        val styleAsAButton: Boolean?,
        @SerializedName("is_primary")
        val isPrimary: Boolean?,
        @SerializedName("placeholder")
        val placeHolder: String?,
        @SerializedName("text")
        val text: String?
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
        val verify: Boolean? ,
        @SerializedName("regex")
        val regex: String?  ,
        @SerializedName("regex_message")
        val regexMessage: String?

)

data class DynamicFormReq(
        @SerializedName("form_id")
        val formId: String?

)