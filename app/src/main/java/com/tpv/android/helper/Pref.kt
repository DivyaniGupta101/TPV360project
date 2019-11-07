package com.tpv.android.helper

import com.chibatching.kotpref.KotprefModel
import com.chibatching.kotpref.gsonpref.gsonNullablePref
import com.tpv.android.model.UserDetail

object Pref : KotprefModel() {

    var token by nullableStringPref()
    var user by gsonNullablePref<UserDetail>()
}