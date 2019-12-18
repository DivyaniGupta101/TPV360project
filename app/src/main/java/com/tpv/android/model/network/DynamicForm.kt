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
        val values: Values?
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
        val length: String?,
        @SerializedName("verify")
        val verify: Boolean?

)

data class Values(

        @SerializedName("first_name")
        var firstName: String?,
        @SerializedName("middle_initial")
        var middleName: String?,
        @SerializedName("last_name")
        var lastName: String?,

        @SerializedName("value")
        var value: String?,

        @SerializedName("billing_address_1")
        var billingAddress1: String?,
        @SerializedName("billing_address_2")
        var billingAddress2: String?,
        @SerializedName("billing_zipcode")
        var billingZipcode: String?,
        @SerializedName("billing_city")
        var billingCity: String?,
        @SerializedName("billing_state")
        var billingState: String?,
        @SerializedName("service_address_1")
        var serviceAddress1: String?,
        @SerializedName("service_address_2")
        var serviceAddress2: String?,
        @SerializedName("service_zipcode")
        var serviceZipcode: String?,
        @SerializedName("service_city")
        var serviceCity: String?,
        @SerializedName("service_state")
        var serviceState: String?,
        @SerializedName("is_billing_address_same_as_service_address")
        var isAddressSame: Boolean?,
        @SerializedName("billing_unit")
        var billingUnit: String?,
        @SerializedName("service_unit")
        var serviceUnit: String?,
        @SerializedName("billing_country")
        var billingCountry: String?,
        @SerializedName("service_country")
        var serviceCountry: String?,

        var serviceAddress: String?,
        var billingAddress: String?,
        var serviceLat: String?,
        var serviceLng: String?,
        var billingLat: String?,
        var billingLng: String?,

        @SerializedName("address_1")
        var address1: String?,
        @SerializedName("address_2")
        var address2: String?,
        @SerializedName("zipcode")
        var zipcode: String?,
        @SerializedName("city")
        var city: String?,
        @SerializedName("state")
        var state: String?,
        @SerializedName("unit")
        var unit: String?,
        @SerializedName("country")
        var country: String?,

        var address: String?,
        var lat: String?,
        var lng: String?

)

data class DynamicFormReq(
        @SerializedName("clientid")
        val clientid: String?,
        @SerializedName("commodity")
        val commodity: String?,
        @SerializedName("programid")
        val programid: String?
)