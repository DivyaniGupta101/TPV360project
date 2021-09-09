package com.tpv.android.model.network

import com.google.gson.annotations.SerializedName

data class ResponseCancelEnrollement(

	@field:SerializedName("data")
	val data: String? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)
