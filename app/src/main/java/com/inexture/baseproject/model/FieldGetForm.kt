package com.inexture.baseproject.model

import com.google.gson.annotations.SerializedName

data class FieldGetForm(
        @SerializedName("firstnameplaceholder")
        var firstnameplaceholder: String?,
        @SerializedName("firstname_name")
        var firstnameName: String?,
        @SerializedName("middlenameplaceholder")
        var middlenameplaceholder: String?,
        @SerializedName("middlename_name")
        var middlenameName: String?,
        @SerializedName("lastnameplaceholder")
        var lastnameplaceholder: String?,
        @SerializedName("lastname_name")
        var lastnameName: String?,
        @SerializedName("address1_placeholder")
        var address1Placeholder: String?,
        @SerializedName("address1_name")
        var address1Name: String?,
        @SerializedName("address2_placeholder")
        var address2Placeholder: String?,
        @SerializedName("address2_name")
        var address2Name: String?,
        @SerializedName("city_placeholder")
        var cityPlaceholder: String?,
        @SerializedName("city_name")
        var cityName: String?,
        @SerializedName("state_placeholder")
        var statePlaceholder: String?,
        @SerializedName("state_name")
        var stateName: String?,
        @SerializedName("zipcode_placeholder")
        var zipcodePlaceholder: String?,
        @SerializedName("zipcode_name")
        var zipcodeName: String?
)