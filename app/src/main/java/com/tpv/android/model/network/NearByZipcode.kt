package com.tpv.android.model.network
import com.google.gson.annotations.SerializedName



data class PostalCode(
    @SerializedName("adminCode1")
    val adminCode1: String?,
    @SerializedName("adminCode2")
    val adminCode2: String?,
    @SerializedName("adminName1")
    val adminName1: String?,
    @SerializedName("adminName2")
    val adminName2: String?,
    @SerializedName("countryCode")
    val countryCode: String?,
    @SerializedName("distance")
    val distance: String?,
    @SerializedName("lat")
    val lat: Double?,
    @SerializedName("lng")
    val lng: Double?,
    @SerializedName("placeName")
    val placeName: String?,
    @SerializedName("postalCode")
    val postalCode: String?
)