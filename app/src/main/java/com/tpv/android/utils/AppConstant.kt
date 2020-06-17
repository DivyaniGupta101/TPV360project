package com.tpv.android.utils

import com.chibatching.kotpref.KotprefModel

object AppConstant : KotprefModel() {

    var GEO_LOCATION_ENABLE by booleanPref(false)
    var CURRENT_GEO_LOCATION by booleanPref(false)
    var GEO_LOCATION_RADIOUS by stringPref("100")
    const val ADDRESSPICKER_KEY = "AIzaSyB5w9xL068s7yS9muLzbpQvSp6_WK1k0tE"
    const val TICKET_USERNAME = "UCKcr5mXc6kIJ60kOVV"
    const val PLACE_COUNTRY = "US"
    const val FIRSTNAME = "first_name"
    const val MIDDLENAME = "middle_initial"
    const val LASTNAME = "last_name"
    const val ADDRESS1 = "address_1"
    const val ADDRESS2 = "address_2"
    const val ZIPCODE = "zipcode"
    const val CITY = "city"
    const val STATE = "state"
    const val UNIT = "unit"
    const val COUNTRY = "country"
    const val LAT = "lat"
    const val LNG = "lng"
    const val VALUE = "value"
    const val OPTIONS = "options"
    const val BILLINGADDRESS1 = "billing_address_1"
    const val BILLINGADDRESS2 = "billing_address_2"
    const val BILLINGZIPCODE = "billing_zipcode"
    const val BILLINGCITY = "billing_city"
    const val BILLINGSTATE = "billing_state"
    const val SERVICEADDRESS1 = "service_address_1"
    const val SERVICEADDRESS2 = "service_address_2"
    const val SERVICEZIPCODE = "service_zipcode"
    const val SERVICECITY = "service_city"
    const val SERVICESTATE = "service_state"
    const val BILLINGUNIT = "billing_unit"
    const val SERVICEUNIT = "service_unit"
    const val BILLINGCOUNTRY = "billing_country"
    const val SERVICECOUNTRY = "service_country"
    const val BILLINGLAT = "billing_lat"
    const val BILLINGLNG = "billing_lng"
    const val SERVICELAT = "service_lat"
    const val SERVICELNG = "service_lng"
    const val VOICE = "voice"
    const val SMS = "sms"
    const val EN = "en"
    const val ES = "es"
    const val CLOCKIN = "clock_in"
    const val CLOCKOUT = "clock_out"
    const val BREAKIN = "break_in"
    const val BREAKOUT = "break_out"
    const val ARRIVALIN = "arrival_in"
    const val ARRIVALOUT = "arrival_out"
    const val SALESAGENT = "salesagent"
    const val CLIENT = "client"

}
