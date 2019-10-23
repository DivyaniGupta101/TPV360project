package com.tpv.android.model

import com.google.gson.annotations.SerializedName

data class DuelFuel(
        @SerializedName("type")
        var type: String?,
        @SerializedName("label_text")
        var labelText: Any?,
        @SerializedName("width")
        var width: String?,
        @SerializedName("required")
        var required: Boolean?,
        @SerializedName("placeholder_text")
        var placeholderText: Any?,
        @SerializedName("fields")
        var fields: com.tpv.android.model.FieldDuelFuel?,
        @SerializedName("gasradiolabel")
        var gasradiolabel: List<String>?,
        @SerializedName("electricradiolabel")
        var electricradiolabel: List<String>?,
        @SerializedName("selectboxlabel")
        var selectboxlabel: List<String>?

)