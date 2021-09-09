package com.tpv.android.model.network

import com.google.gson.annotations.SerializedName

data class ResponseCustomerInformation(

	@field:SerializedName("tmp_data")
	val tmpData: List<TmpDataItem?>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class ProgramDetailItem(

	@field:SerializedName("msf")
	val msf: String? = null,

	@field:SerializedName("customerType")
	val customerType: String? = null,

	@field:SerializedName("code")
	val code: String? = null,

	@field:SerializedName("rate")
	val rate: String? = null,

	@field:SerializedName("etf")
	val etf: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("utility")
	val utility: String? = null,

	@field:SerializedName("term")
	val term: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("utilityName")
	val utilityName: String? = null
)

data class TmpDataItem(

	@field:SerializedName("Email")
	val email: String? = null,

	@field:SerializedName("service_address_1")
	val serviceAddress1: String? = null,

	@field:SerializedName("program_detail")
	val programDetail: List<ProgramDetailItem?>? = null,

	@field:SerializedName("service_address_2")
	val serviceAddress2: String? = null,

	@field:SerializedName("city")
	val city: String? = null,

	@field:SerializedName("county")
	val county: String? = null,

	@field:SerializedName("billing_country")
	val billingCountry: String? = null,

	@field:SerializedName("middle_initial")
	val middleInitial: String? = null,

	@field:SerializedName("last_name")
	val lastName: String? = null,

	@field:SerializedName("billing_county")
	val billingCounty: String? = null,

	@field:SerializedName("service_country")
	val serviceCountry: String? = null,

	@field:SerializedName("billing_zipcode")
	val billingZipcode: String? = null,

	@field:SerializedName("temp_lead_id")
	val tempLeadId: String? = null,

	@field:SerializedName("zipcode")
	val zipcode: String? = null,

	@field:SerializedName("billing_city")
	val billingCity: String? = null,

	@field:SerializedName("billing_state")
	val billingState: String? = null,

	@field:SerializedName("parent_id")
	val parentId: Int? = null,

	@field:SerializedName("billing_address_1")
	val billingAddress1: String? = null,

	@field:SerializedName("billing_address_2")
	val billingAddress2: String? = null,

	@field:SerializedName("phone_number")
	val phoneNumber: String? = null,

	@field:SerializedName("state")
	val state: String? = null,

	@field:SerializedName("first_name")
	val firstName: String? = null,

	@field:SerializedName("Account_Number")
	val accountNumber: String? = null
)
