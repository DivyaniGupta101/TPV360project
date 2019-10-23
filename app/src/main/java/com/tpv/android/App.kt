package com.tpv.android

import android.app.Application
import com.chibatching.kotpref.Kotpref

class App : Application() {

    companion object {

    }

    override fun onCreate() {
        super.onCreate()
        Kotpref.init(applicationContext)


    }
}