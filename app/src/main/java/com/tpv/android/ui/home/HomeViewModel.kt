package com.tpv.android.ui.home

import android.location.Location
import com.tpv.android.data.AppRepository

class HomeViewModel : com.tpv.android.network.resources.CoroutineScopedViewModel() {
    var location: Location? = null

    fun getProfile() = with(AppRepository) {
        getProfileDetailCall()
    }

    fun logout() = with(AppRepository)
    {
        logoutCall()
    }

}