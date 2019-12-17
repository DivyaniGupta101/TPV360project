package com.tpv.android.utils

import com.chibatching.kotpref.KotprefModel

object AppConstant : KotprefModel() {

    var GEO_LOCATION_ENABLE by booleanPref(true)
    var GEO_LOCATION_RADIOUS by stringPref("100")
    const val ADDRESSPICKER_KEY = "AIzaSyB5w9xL068s7yS9muLzbpQvSp6_WK1k0tE"
    const val PLACE_COUNTRY = "US"
}
