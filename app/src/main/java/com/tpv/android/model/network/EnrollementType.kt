package com.tpv.android.model.network

import com.google.gson.annotations.SerializedName

data class EnrollementType(

	@SerializedName("data")
	val data: Data,

	@SerializedName("message")
	val message: String,

	@SerializedName("status")
	val status: String
)

data class Data(

	@SerializedName("is_enable_image_upload_mandatory")
	val isEnableImageUploadMandatory: Int
)
