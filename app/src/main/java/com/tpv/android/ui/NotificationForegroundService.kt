package com.tpv.android.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavDeepLinkBuilder
import com.google.android.gms.location.LocationCallback
import com.livinglifetechway.k4kotlin.core.TAG
import com.livinglifetechway.k4kotlin.core.orFalse
import com.livinglifetechway.k4kotlin.core.orZero
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.tpv.android.R
import com.tpv.android.data.AppRepository.setLocationCall
import com.tpv.android.model.network.AgentLocationRequest
import com.tpv.android.ui.home.TransparentActivity
import com.tpv.android.utils.LocationHelper
import com.tpv.android.ui.home.HomeActivity
import com.tpv.android.utils.infoDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class NotificationForegroundService : Service() {
    private val NOTIFICATION_CHANNEL_ID_DEFAULT = "default"
    private val NOTIFICATION_ID = 108
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)
    private var locationManager: LocationManager? = null
    private var locationListener: LocationListener? = null
    private var context = this

    companion object {
        var LOCATIONKEY = "location"
        var location: Location? = null
        var STOPACTION = "STOP_ACTION"
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.getBundleExtra(LOCATIONKEY) != null) location = intent?.getBundleExtra(LOCATIONKEY) as Location

        if (intent?.getAction() != null && intent.action.equals(STOPACTION)) {
            stopForeground(true)
            hideNotification()
            stopLocationUpdate()
        } else {
            setUpNotification()
            handleCallBackOfAutoLocationUpdate()
            updateLocation()
        }

        return START_NOT_STICKY
    }

    private fun setUpNotification() {
        val notificationManager = context.let {
            ContextCompat.getSystemService(
                    it,
                    NotificationManager::class.java
            )
        } as NotificationManager
        createChannel(NOTIFICATION_CHANNEL_ID_DEFAULT, NOTIFICATION_CHANNEL_ID_DEFAULT)
        context.let { notificationManager.sendNotification("You are currently Clocked-In", it) }
    }

    private fun handleCallBackOfAutoLocationUpdate() {
        locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationCallback(), LocationListener {
            override fun onLocationChanged(loc: Location?) {
                if (loc != null) {
                    if (loc.hasAccuracy()) {
                        location = loc
                        updateLocationApiCall()
                    }
                }
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            }

            override fun onProviderEnabled(provider: String?) {
            }

            override fun onProviderDisabled(provider: String?) {
            }

        }

    }

    private fun updateLocationApiCall() {
        uiScope.launch {
            this.setLocationCall(AgentLocationRequest(lat = location?.latitude.toString(), lng =
            location?.longitude.toString()))
        }
    }


    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_LOW
            )

            val notificationManager = context.getSystemService(
                    NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context) {

        val pendingIntent = NavDeepLinkBuilder(context)
                .setComponentName(HomeActivity::class.java)
                .setGraph(R.navigation.nav_graph)
                .setDestination(R.id.clockTimeFragment)
                .createPendingIntent()

        val builder = NotificationCompat.Builder(
                applicationContext,
                NOTIFICATION_CHANNEL_ID_DEFAULT)
                .setSmallIcon(R.drawable.ic_menu_white_access_time_32dp)
                .setContentText(messageBody)
                .setOngoing(true)
                .setContentIntent(pendingIntent)

        startForeground(NOTIFICATION_ID, builder.build())
        notify(NOTIFICATION_ID, builder.build())
    }

    private fun hideNotification() {
        context.let { NotificationManagerCompat.from(it) }.cancel(NOTIFICATION_ID)
    }

    /**
     * Update location every one minute and minimum distance is more than 50 meter
     */
    @SuppressLint("MissingPermission")
    public fun updateLocation() {
        if (locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER).orFalse()) {

            locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000,
                    50f,
                    locationListener)

        } else {
            getLocation()
//            context.infoDialog(subTitleText = getString(R.string.msg_gps_location))
        }
    }

    /**
     * Remove location update
     */
    private fun stopLocationUpdate() {
        locationManager?.removeUpdates(locationListener)
    }

    private fun getLocation() = runWithPermissions(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    ) {
        uiScope.launch {
            location = context?.let { LocationHelper.getLastKnownLocation(it) }


            if (!locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER).orFalse()) {
                context?.infoDialog(subTitleText = getString(R.string.msg_gps_location))
            } else {
                if (location == null) {
                    try {
                        startActivityForResult(context as Activity, Intent(context, TransparentActivity::class.java), TransparentActivity.REQUEST_CHECK_SETTINGS, null)
                        location = context?.let { LocationHelper.getLastKnownLocation(it) }
                    } catch (e: Exception) {
                        Log.e(TAG, "getResult: ", e)
                    }
                } else {
                    if (location?.time.orZero() >= 1000) {

                        for (i in 1..3) {
                            if (location?.time.orZero() < 1000) {
                                break
                            } else {
                                try {
                                    startActivityForResult(context as Activity, Intent(context, TransparentActivity::class.java), TransparentActivity.REQUEST_CHECK_SETTINGS, null)
                                    location = context?.let { LocationHelper.getLastKnownLocation(it) }
                                } catch (e: Exception) {
                                    Log.e(TAG, "getResult: ", e)
                                }
                            }
                        }
                        if (location?.time.orZero() >= 1000) {
                            context.infoDialog(subTitleText = getString(R.string.msg_unable_detect_location))
                        }
                    }
                }
            }

        }
    }
}