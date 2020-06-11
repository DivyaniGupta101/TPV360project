package com.tpv.android.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import com.google.android.gms.location.LocationServices
import com.tpv.android.ui.NotificationForegroundService
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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
    suspend fun isBestLocation(context: Context, location: Location?): Boolean =
            suspendCancellableCoroutine<Boolean> { continuation ->
                if (location == null) {
                    continuation.resume(false)
                    return@suspendCancellableCoroutine
                }
                val timeOfLocation = Math.abs(location.time - System.currentTimeMillis())
                if (timeOfLocation >= 1000) {
                    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
                    val listener = object : LocationListener {
                        override fun onLocationChanged(location: Location?) {
                            locationManager?.removeUpdates(this)
                            NotificationForegroundService.location = location
                            if (continuation.isActive)
                                continuation.resume(true)
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
                } else {
                    continuation.resume(true)
                }
            }
}