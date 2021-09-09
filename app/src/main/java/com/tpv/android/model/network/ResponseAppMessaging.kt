package com.tpv.android.model.network

import com.google.gson.annotations.SerializedName

data class ResponseAppMessaging(

	@SerializedName("data")
	val data: List<DataItem>,

	@SerializedName("success")
	val success: Boolean,

	@SerializedName("message")
	val message: String
)

data class DataItem(

	@SerializedName("updated_at")
	val updatedAt: String,

	@SerializedName("created_at")
	val createdAt: String,

	@SerializedName("id")
	val id: Int,

	@SerializedName("message")
	val message: String,

	@SerializedName("client_id")
	val clientId: Int,

	@SerializedName("status")
	val status: Int
)
