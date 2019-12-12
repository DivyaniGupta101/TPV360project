package com.tpv.android.utils

import com.chibatching.kotpref.KotprefModel

object AppConstant : KotprefModel() {

    const val ZIPCODE_GEONAMES_API_URL = "http://api.geonames.org/findNearbyPostalCodesJSON"
    const val ZIPCODE_GEONAMES_API_REQUEST_USERNAME = "rinal.shah"

    val LOCATION_EXPIRED_TIMEOUT by longPref(2 * 60 * 1000)
    var GEO_LOCATION_ENABLE by booleanPref(true)
    var GEO_LOCATION_RADIOUS by stringPref("")
}
