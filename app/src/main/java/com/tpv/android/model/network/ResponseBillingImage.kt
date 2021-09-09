package com.tpv.android.model.network

import com.google.gson.annotations.SerializedName

data class ResponseBillingImage(

	@field:SerializedName("leadbilling_data")
	val leadbillingData: LeadbillingData? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class LeadbillingData(

	@field:SerializedName("url")
	val url: String? = null
)
