package com.tpv.android.model.network

import com.google.gson.annotations.SerializedName

data class RequestCustomer(

	@field:SerializedName("tmp_lead_ids")
	val tmpLeadIds: List<String?>? = null
)
