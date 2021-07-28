package com.tpv.android.model.network


import com.google.gson.annotations.SerializedName

data class DynamicSettingsReq(
        @SerializedName("form_id")
        val formId: String?
)

data class DynamicSettingResponse(
        @SerializedName("is_enable_enroll_by_state")
        val isEnableEnrollByState: Boolean?,
        @SerializedName("is_enable_recording")
        val isEnableRecording: Boolean?,
        @SerializedName("is_enable_enroll_by_zip")
        val isEnableEnrollByZip: Boolean?,
        @SerializedName("is_enable_image_upload")
        val isEnableImageUpload: Boolean?,
        @SerializedName("is_enable_image_upload_mandatory")
        val isEnableImageUploadMandatory: Boolean?,
        @SerializedName("kiwi_enable_image_upload")
        val kiwi_enable_image_upload: Boolean?,
        @SerializedName("le_client_enrollment_type")
        val le_client_enrollment_type: Boolean?,
        @SerializedName("is_enable_duel_fuel_mandatory")
        val is_enable_duel_fuel_mandatory: Boolean?


)