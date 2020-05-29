package com.tpv.android.model.network

import com.google.gson.annotations.SerializedName

data class ScheduleTPVCallRequest(
        @SerializedName("call_now")
        var callNow:Boolean? ,
        @SerializedName("call_lang")
        var callLang:String? ,
        @SerializedName("telesale_id")
        var telesaleId:String?
)