package com.tpv.android.utils

import com.chibatching.kotpref.KotprefModel
import com.tpv.android.BuildConfig

object AppConfig : KotprefModel() {
    var BASEURL = if (BuildConfig.DEBUG) "https://xyzenergy.tpv360.com/api/" else "https://spark.tpv.plus/api/"
}
