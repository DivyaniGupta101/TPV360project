package com.tpv.android.ui.salesagent.home.profile

import com.tpv.android.data.AppRepository
import com.tpv.android.network.resources.CoroutineScopedViewModel

class ProfileViewModel : CoroutineScopedViewModel() {
    fun getProfile() = with(AppRepository) {
        getProfileDetailCall()
    }

    fun getTimeZone() = with(AppRepository)
    {
        getTimeZoneCall()
    }
}