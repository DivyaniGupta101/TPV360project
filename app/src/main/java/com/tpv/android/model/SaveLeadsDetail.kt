package com.tpv.android.model

import com.google.gson.annotations.SerializedName

data class SaveLeadsDetailResp(
        @SerializedName("id")
        var id: Int?,
        @SerializedName("reference_id")
        var referenceId: String?

)

data class SaveLeadsDetailReq(
        @SerializedName("clientid")
        val clientid: String?,
        @SerializedName("commodity")
        val commodity: String?,
        @SerializedName("electricprogramid")
        val electricprogramid: String?,
        @SerializedName("electricutility_id")
        val electricutilityId: String?,
        @SerializedName("fields")
        val fields: List<ServiceDetail>?,
        @SerializedName("gasprogramid")
        val gasprogramid: String?,
        @SerializedName("gasutility_id")
        val gasutilityId: String?,
        @SerializedName("utility_id")
        val utilityId: String?,
        @SerializedName("programid")
        val programId: String?,
        @SerializedName("zipcode")
        val zipcode: String?
)

data class ServiceDetail(
        @SerializedName("Authorized First name")
        val authorizedFirstName: String?,
        @SerializedName("Authorized Last name")
        val authorizedLastName: String?,
        @SerializedName("Authorized Middle initial")
        val authorizedMiddleInitial: String?,
        @SerializedName("country_code")
        val countryCode: String?,
        @SerializedName("Electric Account Number")
        val electricAccountNumber: String?,
        @SerializedName("ElectricBillingAddress")
        val electricBillingAddress: String?,
        @SerializedName("ElectricBillingAddress2")
        val electricBillingAddress2: String?,
        @SerializedName("ElectricBillingCity")
        val electricBillingCity: String?,
        @SerializedName("Electric Billing First name")
        val electricBillingFirstName: String?,
        @SerializedName("Electric Billing Last name")
        val electricBillingLastName: String?,
        @SerializedName("Electric Billing Middle initial")
        val electricBillingMiddleInitial: String?,
        @SerializedName("ElectricBillingState")
        val electricBillingState: String?,
        @SerializedName("ElectricBillingZip")
        val electricBillingZip: String?,
        @SerializedName("Email")
        val email: String?,
        @SerializedName("Gas Account Number")
        val gasAccountNumber: String?,
        @SerializedName("Gas auth relationship")
        val gasAuthRelationship: String?,
        @SerializedName("GasBillingAddress")
        val gasBillingAddress: String?,
        @SerializedName("GasBillingAddress2")
        val gasBillingAddress2: String?,
        @SerializedName("GasBillingCity")
        val gasBillingCity: String?,
        @SerializedName("Gas Billing First name")
        val gasBillingFirstName: String?,
        @SerializedName("Gas Billing Last name")
        val gasBillingLastName: String?,
        @SerializedName("Gas Billing Middle initial")
        val gasBillingMiddleInitial: String?,
        @SerializedName("GasBillingState")
        val gasBillingState: String?,
        @SerializedName("GasBillingZip")
        val gasBillingZip: String?,
        @SerializedName("GasServiceAddress")
        val gasServiceAddress: String?,
        @SerializedName("GasServiceAddress2")
        val gasServiceAddress2: String?,
        @SerializedName("GasServiceCity")
        val gasServiceCity: String?,
        @SerializedName("GasServiceState")
        val gasServiceState: String?,
        @SerializedName("GasServiceZip")
        val gasServiceZip: String?,
        @SerializedName("Is the billing address the same as the service address")
        val isTheBillingAddressTheSameAsTheServiceAddress: String?,
        @SerializedName("Phone Number")
        val phoneNumber: String?,
        @SerializedName("ServiceAddress")
        val serviceAddress: String?,
        @SerializedName("ServiceAddress2")
        val serviceAddress2: String?,
        @SerializedName("ServiceCity")
        val serviceCity: String?,
        @SerializedName("ServiceState")
        val serviceState: String?,
        @SerializedName("ServiceZip")
        val serviceZip: String?,
        @SerializedName("BillingAddress")
        val billingAddress: String?,
        @SerializedName("BillingAddress2")
        val billingAddress2: String?,
        @SerializedName("Relationship")
        val relationShip: String?,
        @SerializedName("BillingZip")
        val billingZip: String?,
        @SerializedName("BillingCity")
        val billingCity: String?,
        @SerializedName("BillingState")
        val billingState: String?,
        @SerializedName("Billing First name")
        val billingFirstName: String?,
        @SerializedName("Billing Middle initial")
        val billingMiddleInitial: String?,
        @SerializedName("Billing Last name")
        val billingLastName: String?,
        @SerializedName("Account Number")
        val accountNumber: String?

)