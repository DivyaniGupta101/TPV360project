package com.tpv.android.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
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
}