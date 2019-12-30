package com.tpv.android.helper

import com.google.android.libraries.places.api.model.Place
import com.livinglifetechway.k4kotlin.core.orFalse
import com.tpv.android.model.internal.AddressComponent

fun addressComponents(place: Place): AddressComponent? {
    val placeComponent = AddressComponent("", "", "", "", "", "", "", "", "")

    val addressComponents = place.addressComponents?.asList()

    var city = addressComponents?.find { it.types.find { it == "locality" } != null }?.name
    if (city.isNullOrEmpty()) {
        city = addressComponents?.find { it.types.find { it == "administrative_area_level_2" } != null }?.name
    }

    val neighborhood = addressComponents?.find { it.types.find { it == "neighborhood" } != null }?.name
    val subLocalityLevelOne = addressComponents?.find { it.types.find { it == "sublocality_level_1" } != null }?.name
    var address2 = ""
    if (neighborhood?.isNotEmpty().orFalse() || subLocalityLevelOne?.isNotEmpty().orFalse()) {
        address2 = neighborhood + subLocalityLevelOne
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