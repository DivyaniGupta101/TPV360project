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
    LOGOUT("logout"),
    SUPPORT("support"),
    TIMECLOCK("timeclock")
}

enum class ClientMenuItem(val value: String) {
    DASHBOARD("dashboard"),
    PROFILE("profile"),
    REPORTS("reports"),
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

enum class SortByItem(val value: String) {
    LEADIDASC("lead_id_asc"),
    LEADIDDES("lead_id_desc"),
    REFERENCEIDASC("reference_id_asc"),
    REFERENCEIDDES("reference_id_desc"),
    ALERTSTATUSASC("alert_status_asc"),
    ALERTSTATUSDES("alert_status_desc"),
    LEADSTATUSASC("lead_status_asc"),
    LEADSTATUSDES("lead_status_desc"),
    CLIENTNAMEASC("client_name_asc"),
    CLIENTNAMEDES("client_name_desc"),
    SALESCENTERNAMEASC("salescenter_name_asc"),
    SALESCENTERNAMEDES("salescenter_name_desc"),
    SALESCENTERLOCATIONADDRESSASC("salesceneter_location_address_asc"),
    SALESCENTERLOCATIONADDRESSDES("salesceneter_location_address_desc"),
    SALESAGENTNAMEASC("salesagent_name_asc"),
    SALESAGENTNAMEDES("salesagent_name_desc"),
    DATEOFSUBMISSIONASC("date_of_submission_asc"),
    DATEOFSUBMISSIONDES("date_ of_ submission_ desc"),
    DATEOFTPVASC("date_of_tpv_asc"),
    DATEOFTPVDES("date_ of_ tpv_ desc"),
    SALESCENTERLOCATIONNAMEASC("salescenter_location_name_asc"),
    SALESCENTERLOCATIONNAMEDES("salescenter_location_name_desc"),


}