package com.inexture.baseproject.model

import com.google.gson.annotations.SerializedName

data class GetForm(
        @SerializedName("type")
        var type: String? = null,
        @SerializedName("label_text")
        var labelText: String? = null,
        @SerializedName("required")
        var required: Boolean? = null,
        @SerializedName("fields")
        var fields: FieldGetForm? = null,
        @SerializedName("placeholder_text")
        var placeholderText: Any? = null,
        @SerializedName("width")
        var width: String? = null,
        @SerializedName("radiolabel")
        var radiolabel: List<String>? = null,
        @SerializedName("selectboxlabel")
        var selectboxlabel: List<String>? = null

)