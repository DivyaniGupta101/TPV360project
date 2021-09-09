package com.tpv.android.model.network

import com.google.gson.annotations.SerializedName

data class Requentutilityid(

	@field:SerializedName("utility_id")
	val utilityId: List<String>,
	@SerializedName("state_id")
	val state_id: String?,
	@SerializedName("zipcode")
	val zipcode: String?
)
