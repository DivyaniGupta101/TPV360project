package com.tpv.android.helper

import com.google.android.libraries.places.api.model.Place
import com.tpv.android.model.internal.AddressComponent

fun addressComponents(place: Place): AddressComponent? {
    val placeComponent = AddressComponent("", "", "", "", "", "", "", "", "", "")

    val addressComponents = place.addressComponents?.asList()

    var city = addressComponents?.find { it.types.find { it == "locality" } != null }?.name
    if (city.isNullOrBlank()) {
        city = addressComponents?.find { it.types.find { it == "sublocality_level_1" } != null }?.name
    }

    val streetNumber = addressComponents?.find { it.types.find { it == "street_number" } != null }?.name
    val route = addressComponents?.find { it.types.find { it == "route" } != null }?.name
    var address2 = ""
    if (!streetNumber.isNullOrBlank()) {
        address2 = streetNumber
    }
    if (!route.isNullOrBlank()) {
        address2 = if (address2.isEmpty()) {
            route
        } else {
            " $route"
        }
    }

    placeComponent.address = place.address.orEmpty()
    placeComponent.addressLine1 = place.name.orEmpty()
    placeComponent.addressLine2 = address2
    placeComponent.city = city.orEmpty()
    placeComponent.state = addressComponents?.find { it.types.find { it == "administrative_area_level_1" } != null }?.name.orEmpty()
    placeComponent.latitude = place.latLng?.latitude.toString()
    placeComponent.longitude = place.latLng?.longitude.toString()
    placeComponent.zipcode = addressComponents?.find { it.types.find { it == "postal_code" } != null }?.name.orEmpty()
    placeComponent.country = addressComponents?.find { it.types.find { it == "country" } != null }?.name.orEmpty()

    return placeComponent
}