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
        @SerializedName("multiple_enrollment")
        val mutipleEnrollment: Int,
        @SerializedName("validations")
        val validations: Validations?,
        @SerializedName("values")
        var values: LinkedHashMap<String, Any>?,
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
        @SerializedName("is_allow_copy")
        val isAllowCopy: Boolean?,
        @SerializedName("is_auto_caps")
        val isAllCaps: Boolean?,
        @SerializedName("placeholder")
        var placeHolder: String?,
        @SerializedName("text")
        val text: String?,
        @SerializedName("is_multienrollment")
        val is_multienrollment: Boolean?
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
        val verify: Boolean?,
        @SerializedName("regex")
        var regex: String?,
        @SerializedName("regex_message")
        var regexMessage: String?

)

data class DynamicFormReq(
        @SerializedName("form_id")
        val formId: String?
//        @SerializedName("commodity_id")
//         val commodity_id: List<String?>? = null

)