package com.tpv.android.model.network


import com.google.gson.annotations.SerializedName

data class ProgramsDetail(
    @SerializedName("id")
    val id: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("utility")
    val utility: String?
)