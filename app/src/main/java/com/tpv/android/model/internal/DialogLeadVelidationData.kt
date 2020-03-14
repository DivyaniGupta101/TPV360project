package com.tpv.android.model.internal

import com.tpv.android.model.network.LeadValidationError

data class DialogLeadValidationData(var title: String?,
                                    val errors: List<LeadValidationError>,
                                    var positiveButtonText: String?,
                                    var negativeButtonText: String?
)