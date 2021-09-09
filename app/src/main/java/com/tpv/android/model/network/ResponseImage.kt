package com.tpv.android.model.network

import com.google.gson.annotations.SerializedName

data class ResponseImage(

	@field:SerializedName("image_upload")
	val imageUpload: ImageUpload,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: String
)

data class ImageUpload(

	@field:SerializedName("is_enable_image_upload")
	val isEnableImageUpload: Boolean,

	@field:SerializedName("is_enable_image_upload_mandatory")
	val isEnableImageUploadMandatory: Boolean
)
