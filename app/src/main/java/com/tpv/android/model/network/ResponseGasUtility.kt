package com.tpv.android.model.network

import com.google.gson.annotations.SerializedName

data class ResponseGasUtility(

	@field:SerializedName("per_page")
	val perPage: Int,

	@field:SerializedName("total")
	val total: Int,

	@field:SerializedName("last_page")
	val lastPage: Int,

	@field:SerializedName("gasdata")
	val gasdata: List<GasdataItem>,

	@field:SerializedName("next_page_url")
	val nextPageUrl: String,

	@field:SerializedName("from")
	val from: Int,

	@field:SerializedName("to")
	val to: Int,

	@field:SerializedName("prev_page_url")
	val prevPageUrl: String,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("totalrecords")
	val totalrecords: Int,

	@field:SerializedName("current_page")
	val currentPage: Int,

	@field:SerializedName("status")
	val status: String
)

data class GasCustomFieldsItem(

	@field:SerializedName("label")
	val label: String,

	@field:SerializedName("value")
	val value: String
)

data class GasdataItem(

	@field:SerializedName("AccountNumberLength")
	val accountNumberLength: String,

	@field:SerializedName("Rate")
	val rate: String,


	@field:SerializedName("is_selected")
	var is_selected: String,

	@field:SerializedName("PremiseTypeName")
	val premiseTypeName: String,

	@field:SerializedName("Term")
	val term: String,

	@field:SerializedName("commodity_id")
	val commodity_id: String,

	@field:SerializedName("ProgramName")
	val programName: String,

	@field:SerializedName("client_id")
	val clientId: Int,

	@field:SerializedName("ProgramCode")
	val programCode: String,

	@field:SerializedName("monthlysf")
	val monthlysf: String,

	@field:SerializedName("rewardName")
	val rewardName: String,

	@field:SerializedName("State")
	val state: String,

	@field:SerializedName("AccountNumberTypeName")
	val accountNumberTypeName: String,

	@field:SerializedName("utility_id")
	val utilityId: Int,

	@field:SerializedName("Saleschannel")
	val saleschannel: String,

	@field:SerializedName("UnitOfMeasureName")
	val unitOfMeasureName: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("gas_custom_fields")
	val gasCustomFields: List<GasCustomFieldsItem>,

	@field:SerializedName("earlyterminationfee")
	val earlyterminationfee: String
)
