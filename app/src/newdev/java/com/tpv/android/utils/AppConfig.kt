package com.tpv.android.utils

import com.chibatching.kotpref.KotprefModel
import com.tpv.android.BuildConfig

object AppConfig : KotprefModel() {
    var BASEURL = if (BuildConfig.DEBUG) "https://newdev.tpv.plus/api/" else "https://spark.tpv.plus/api/"
}
