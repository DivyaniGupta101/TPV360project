package com.tpv.android.utils.enums


enum class Plan(val value: String) {
    DUALFUEL("Dual Fuel"),
    GASFUEL("Gas"),
    ELECTRICFUEL("Electric")
}

enum class LeadStatus(val value: String) {
    PENDING("pending"),
    VERIFIED("verified"),
    DECLINED("decline"),
    DISCONNECTED("hangup")
}

enum class MenuItem(val value: String) {
    DASHBOARD("dashboard"),
    PROFILE("profile"),
    ENROLL("enroll"),
    LOGOUT("logout")
}

enum class DynamicField() {
    full_name,
    address,
    textbox,
    textarea,
    radio,
    checkbox,
    selectbox,
    separator,
    heading,
    label,
    phone_number,
    email,
    service_and_billing_address

}