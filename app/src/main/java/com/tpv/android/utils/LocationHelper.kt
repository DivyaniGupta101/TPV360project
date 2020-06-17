package com.tpv.android.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.LocationServices
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.ui.salesagent.NotificationForegroundService
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.abs

object LocationHelper {

    /**
     * It provides the last location
     * @param context it needed to pass the location provider client
     * @return [Location] location object
     */
    @SuppressLint("MissingPermission")
    suspend fun getLastKnownLocation(context: Context): Location? =
            suspendCoroutine<Location> { continuation ->
                LocationServices.getFusedLocationProviderClient(context).lastLocation
                        .addOnSuccessListener { location ->
                            continuation.resume(location)
                        }
            }


    @SuppressLint("MissingPermission")
    fun isBestLocation(context: Context, location: Location?): LiveData<Resource<Boolean, APIError>> {
        val liveData = MutableLiveData<Resource<Boolean, APIError>>()
        liveData.value = Resource.loading()

        if (location == null || abs(location.time - System.currentTimeMillis()) >= 100) {

            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
            val handler = Handler()
            var function: () -> Unit = {}

            val listener = object : LocationListener {
                override fun onLocationChanged(location: Location?) {
                    handler.removeCallbacks(function)
                    locationManager?.removeUpdates(this)
                    NotificationForegroundService.location = location
                    liveData.value = Resource.success(true)
                }

                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

                }

                override fun onProviderEnabled(provider: String?) {

                }

                override fun onProviderDisabled(provider: String?) {

                }
            }
            locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, listener)
            locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, listener)

            function = {
                locationManager?.removeUpdates(listener)
                liveData.value = Resource.success(false)
            }
            handler.postDelayed(function, 10 * 1000)

        } else {
            liveData.value = Resource.success(true)
        }

        return liveData
    }
}