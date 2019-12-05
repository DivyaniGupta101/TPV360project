package com.tpv.android.model.network

import com.google.gson.annotations.SerializedName

data class SaveLeadsDetailResp(
        @SerializedName("id")
        var id: String?,
        @SerializedName("reference_id")
        var referenceId: String?

)

data class SaveLeadsDetailReq(
        @SerializedName("clientid")
        val clientid: String? = "",
        @SerializedName("commodity")
        val commodity: String? = "",
        @SerializedName("electricprogramid")
        val electricprogramid: String? = "",
        @SerializedName("electricutility_id")
        val electricutilityId: String? = "",
        @SerializedName("fields")
        val fields: ArrayList<CustomerData?>,
        @SerializedName("gasprogramid")
        val gasprogramid: String? = "",
        @SerializedName("gasutility_id")
        val gasutilityId: String? = "",
        @SerializedName("utility_id")
        val utilityId: String? = "",
        @SerializedName("programid")
        val programId: String? = "",
        @SerializedName("zipcode")
        val zipcode: String? = ""
)

data class CustomerData(
        @SerializedName("Authorized First name")
        var authorizedFirstName: String? = "",
        @SerializedName("Authorized Last name")
        var authorizedLastName: String? = "",
        @SerializedName("Authorized Middle initial")
        var authorizedMiddleInitial: String? = "",
        @SerializedName("country_code")
        var countryCode: String? = "",
        @SerializedName("Electric Account Number")
        var electricAccountNumber: String? = "",
        @SerializedName("ElectricBillingAddress")
        var electricBillingAddress: String? = "",
        @SerializedName("ElectricBillingAddress2")
        var electricBillingAddress2: String? = "",
        @SerializedName("ElectricBillingCity")
        var electricBillingCity: String? = "",
        @SerializedName("Electric Billing First name")
        var electricBillingFirstName: String? = "",
        @SerializedName("Electric Billing Last name")
        var electricBillingLastName: String? = "",
        @SerializedName("Electric Billing Middle initial")
        var electricBillingMiddleInitial: String? = "",
        @SerializedName("ElectricBillingState")
        var electricBillingState: String? = "",
        @SerializedName("ElectricBillingZip")
        var electricBillingZip: String? = "",
        @SerializedName("Email")
        var email: String? = "",
        @SerializedName("Gas Account Number")
        var gasAccountNumber: String? = "",
        @SerializedName("Gas auth relationship")
        var gasAuthRelationship: String? = "",
        @SerializedName("GasBillingAddress")
        var gasBillingAddress: String? = "",
        @SerializedName("GasBillingAddress2")
        var gasBillingAddress2: String? = "",
        @SerializedName("GasBillingCity")
        var gasBillingCity: String? = "",
        @SerializedName("Gas Billing First name")
        var gasBillingFirstName: String? = "",
        @SerializedName("Gas Billing Last name")
        var gasBillingLastName: String? = "",
        @SerializedName("Gas Billing Middle initial")
        var gasBillingMiddleInitial: String? = "",
        @SerializedName("GasBillingState")
        var gasBillingState: String? = "",
        @SerializedName("GasBillingZip")
        var gasBillingZip: String? = "",
        @SerializedName("GasServiceAddress")
        var gasServiceAddress: String? = "",
        @SerializedName("GasServiceAddress2")
        var gasServiceAddress2: String? = "",
        @SerializedName("GasServiceCity")
        var gasServiceCity: String? = "",
        @SerializedName("GasServiceState")
        var gasServiceState: String? = "",
        @SerializedName("GasServiceZip")
        var gasServiceZip: String? = "",
        @SerializedName("Is the billing address the same as the service address")
        var isTheBillingAddressTheSameAsTheServiceAddress: String? = "No",
        @SerializedName("Phone Number")
        var phoneNumber: String? = "",
        @SerializedName("ServiceAddress")
        var serviceAddress: String? = "",
        @SerializedName("ServiceAddress2")
        var serviceAddress2: String? = "",
        @SerializedName("ServiceCity")
        var serviceCity: String? = "",
        @SerializedName("ServiceState")
        var serviceState: String? = "",
        @SerializedName("ServiceZip")
        var serviceZip: String? = "",
        @SerializedName("BillingAddress")
        var billingAddress: String? = "",
        @SerializedName("BillingAddress2")
        var billingAddress2: String? = "",
        @SerializedName("Relationship")
        var relationShip: String? = "",
        @SerializedName("BillingZip")
        var billingZip: String? = "",
        @SerializedName("BillingCity")
        var billingCity: String? = "",
        @SerializedName("BillingState")
        var billingState: String? = "",
        @SerializedName("Billing First name")
        var billingFirstName: String? = "",
        @SerializedName("Billing Middle initial")
        var billingMiddleInitial: String? = "",
        @SerializedName("Billing Last name")
        var billingLastName: String? = "",
        @SerializedName("Account Number")
        var accountNumber: String? = ""

)