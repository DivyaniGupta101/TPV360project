package com.tpv.android

import android.app.Application
import com.chibatching.kotpref.Kotpref
import com.chibatching.kotpref.gsonpref.gson
import com.google.gson.Gson

class App : Application() {

    companion object {

    }

    override fun onCreate() {
        super.onCreate()
        Kotpref.init(applicationContext)
        Kotpref.gson = Gson()

    }
}