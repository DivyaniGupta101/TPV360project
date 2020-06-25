package com.tpv.android.model.network


import com.google.gson.annotations.SerializedName

data class CurrentActivityResponse(
        val activityType: String?,
        @SerializedName("break_time")
        val breakTime: String?,
        @SerializedName("current_time")
        val currentTime: Int?,
        @SerializedName("total_time")
        val totalTime: String?,
        @SerializedName("current_status")
        val currentStatus: String?,
        @SerializedName("transit_time")
        val transitTime: String?,
        @SerializedName("working_time")
        val workingTime: String?,
        @SerializedName("clock_in")
        val isClockIn: Boolean?,
        @SerializedName("break_in")
        val isBreakIn: Boolean?,
        @SerializedName("arrival_in")
        val isArrivalIn: Boolean?

)