package com.tpv.android.ui.home

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.tpv.android.BuildConfig
import com.tpv.android.R
import com.tpv.android.databinding.ActivityTransparentBinding
import com.tpv.android.ui.home.enrollment.dynamicform.DynamicFormFragment.Companion.REQUEST_GPS_SETTINGS
import com.tpv.android.ui.home.enrollment.statement.StatementFragment

class TransparentActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityTransparentBinding

    companion object {
        var REQUEST_CHECK_SETTINGS = 1234
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_transparent)

        createLocationRequest()
    }

    /**
     * create location request and check gps dialog is enabled or not
     */
    private fun createLocationRequest() {
        val locationRequest = LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = locationRequest?.let {
            LocationSettingsRequest.Builder()
                    .addLocationRequest(it)
        }
        val client: SettingsClient? = this.let { LocationServices.getSettingsClient(it) }
        val task: Task<LocationSettingsResponse>? = client?.checkLocationSettings(builder?.build())

        task?.addOnSuccessListener { locationSettingsResponse ->
            setResult(Activity.RESULT_OK)
            finish()
        }

        task?.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(this,
                            REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                    finish()
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        if (!BuildConfig.IS_RECCORDING_ALLOWD)
            this?.window?.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_GPS_SETTINGS) {
                setResult(resultCode)
                finish()
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }
}
