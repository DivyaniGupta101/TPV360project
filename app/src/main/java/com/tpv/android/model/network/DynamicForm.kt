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
        val options: List<Option?>?,
        @SerializedName("style_as_a_button")
        val styleAsAButton: Boolean?,
        @SerializedName("placeholder")
        val placeHolder: String?
)

data class Option(
        @SerializedName("option")
        val option: String?,
        @SerializedName("selected")
        val selected: Boolean?
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
        @SerializedName("billing_address_1")
        val billingAddress1: String?,
        @SerializedName("billing_address_2")
        val billingAddress2: String?,
        @SerializedName("billing_city")
        val billingCity: String?,
        @SerializedName("billing_state")
        val billingState: String?,
        @SerializedName("billing_zipcode")
        val billingZipcode: String?,
        @SerializedName("is_billing_address_same_as_service_address")
        val isBillingAddressSameAsServiceAddress: Boolean?,
        @SerializedName("service_address_1")
        val serviceAddress1: String?,
        @SerializedName("service_address_2")
        val serviceAddress2: String?,
        @SerializedName("service_city")
        val serviceCity: String?,
        @SerializedName("service_state")
        val serviceState: String?,
        @SerializedName("service_zipcode")
        val serviceZipcode: String?,
        @SerializedName("first_name")
        val firstName: String?,
        @SerializedName("middle_initial")
        val middleName: String?,
        @SerializedName("last_name")
        val lastName: String?,
        @SerializedName("value")
        val value: String?
)

data class DynamicFormReq(
        @SerializedName("clientid")
        val clientid: String?,
        @SerializedName("commodity")
        val commodity: String?,
        @SerializedName("programid")
        val programid: String?
)