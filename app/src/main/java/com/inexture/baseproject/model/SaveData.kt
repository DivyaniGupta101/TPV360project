package com.inexture.baseproject.model

import com.google.gson.annotations.SerializedName

data class SaveData(
        @SerializedName("id")
        var id: Int?,
        @SerializedName("reference_id")
        var referenceId: String?

)