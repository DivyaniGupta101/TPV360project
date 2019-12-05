package com.tpv.android.model.network

import com.google.gson.annotations.SerializedName

data class PaginateCommonResp<T>(
        @SerializedName("status")
        var status: String?,
        @SerializedName("message")
        var message: String?,
        @SerializedName("number_of_records")
        var numberOfRecords: Int?,
        @SerializedName("current_page")
        var currentPage: Int?,
        @SerializedName("perpage")
        var perpage: Int?,
        @SerializedName("total")
        var total: Int?,
        @SerializedName("lastPage")
        var lastPage: Int?,
        @SerializedName("data")
        var data: T?

)