package com.tpv.android.utils

import com.tpv.android.BuildConfig

object AppConfig {
    var BASEURL = if (BuildConfig.DEBUG) "https://test.tpv360.com/api/" else "https://spark.tpv.plus/api/"

}