package com.tpv.android.model
import com.google.gson.annotations.SerializedName


data class RecordingResp(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("reference_id")
    val referenceId: String?
)