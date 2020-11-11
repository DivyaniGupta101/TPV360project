package com.tpv.android.model.internal

data class AddressComponent(var address: String?,
                            var addressLine1: String?,
                            var addressLine2: String?,
                            var zipcode: String?,
                            var latitude: String?,
                            var longitude: String?,
                            var state: String?,
                            var city: String,
                            var country: String,
                            var stateSortName: String
)