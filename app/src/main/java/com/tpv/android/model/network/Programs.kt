package com.tpv.android.model.network

import com.google.gson.annotations.SerializedName

data class ProgramsResp(
        @SerializedName("id")
        var id: String?,
        @SerializedName("ProgramCode")
        var programCode: String?,
        @SerializedName("ProgramName")
        var programName: String?,
        @SerializedName("Saleschannel")
        var saleschannel: String?,
        @SerializedName("monthlysf")
        var monthlysf: String?,
        @SerializedName("earlyterminationfee")
        var earlyterminationfee: String?,
        @SerializedName("Rate")
        var rate: String?,
        @SerializedName("utility_id")
        var utilityId: String?,
        @SerializedName("Term")
        var term: String?,
        @SerializedName("State")
        var state: String?,
        @SerializedName("UnitOfMeasureName")
        var unitOfMeasureName: String?,
        @SerializedName("PremiseTypeName")
        var premiseTypeName: String?,
        @SerializedName("AccountNumberTypeName")
        var accountNumberTypeName: String?,
        @SerializedName("AccountNumberLength")
        var accountNumberLength: String?,
        var isSelcected: Boolean? = false,
        var utilityType: String?

)

data class ProgramsReq(
        @SerializedName("utility_id")
        var utilityId: String?
)