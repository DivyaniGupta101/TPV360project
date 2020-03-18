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
    DISCONNECTED("hangup"),
    CANCELLED("cancel")
}

enum class MenuItem(val value: String) {
    DASHBOARD("dashboard"),
    PROFILE("profile"),
    ENROLL("enroll"),
    LOGOUT("logout")
}

enum class DynamicField(val type: String) {
    FULLNAME("fullname"),
    ADDRESS("address"),
    TEXTBOX("textbox"),
    TEXTAREA("textarea"),
    RADIO("radio"),
    CHECKBOX("checkbox"),
    SELECTBOX("selectbox"),
    SEPARATE("separator"),
    HEADING("heading"),
    LABEL("label"),
    PHONENUMBER("phone_number"),
    EMAIL("email"),
    BOTHADDRESS("service_and_billing_address"),
    TEXT("text")


}