package com.tpv.android.network.resources.apierror

data class APIError(
    val message: String? = null,
    val code: Int? = null,
    val status_code: Int? = null,
    val errors: HashMap<String, List<String>>? = null
)