package com.tpv.android.helper

import com.chibatching.kotpref.KotprefModel
import com.chibatching.kotpref.gsonpref.gsonNullablePref
import com.tpv.android.model.network.UserDetail
import java.util.*
import kotlin.collections.HashSet

object Pref : KotprefModel() {

    var token by nullableStringPref()
    var dashBoardUrl by nullableStringPref()
    var user by gsonNullablePref<UserDetail>()
    val searchText by stringSetPref{
        val set = HashSet<String>()
        return@stringSetPref set
    }
}