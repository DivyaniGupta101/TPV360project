package com.tpv.android

import android.app.Activity
import android.app.Application
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.chibatching.kotpref.Kotpref
import com.chibatching.kotpref.gsonpref.gson
import com.google.gson.Gson
import com.tpv.android.helper.Pref
import com.tpv.android.network.UnAuthorizedEventObserver
import com.tpv.android.ui.auth.AuthActivity

class App : Application() {

    var mCurrentActivity: Activity? = null

    companion object {

    }

    override fun onCreate() {
        super.onCreate()
        Kotpref.init(applicationContext)
        Kotpref.gson = Gson()


        // check for unauthorized events
        UnAuthorizedEventObserver.observe {
            mCurrentActivity?.runOnUiThread {
                try {
                    Pref.clear()
                    val intent = Intent(this@App, AuthActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP or
                            Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)

                } catch (e: Exception) {
                    Log.e(TAG, "onCreate: Error showing logout error", e)
                    Pref.clear()
                }
            }
        }

        /**
         * Get Current Activity so that on top of that we display token expired dialogue
         */
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {

            override fun onActivityStarted(activity: Activity?) {
                mCurrentActivity = activity
            }

            override fun onActivityStopped(activity: Activity?) {
                if (mCurrentActivity == activity)
                    mCurrentActivity = null
            }

            override fun onActivityPaused(activity: Activity?) {}
            override fun onActivityDestroyed(activity: Activity?) {}
            override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {}
            override fun onActivityResumed(activity: Activity?) {}
            override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {}
        })
    }
}