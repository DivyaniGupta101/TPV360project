package com.tpv.android.model

import com.google.gson.annotations.SerializedName

data class ContractReq(
        @SerializedName("leadid")
        var leadId: String?
)