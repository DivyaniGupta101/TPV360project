package com.inexture.baseproject.helper

import com.chibatching.kotpref.KotprefModel

object UserPref : KotprefModel() {

    var token by nullableStringPref()
}