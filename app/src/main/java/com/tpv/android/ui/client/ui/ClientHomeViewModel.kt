package com.tpv.android.ui.client.ui


import com.tpv.android.data.AppRepository

class ClientHomeViewModel : com.tpv.android.network.resources.CoroutineScopedViewModel() {

    fun getProfile() = with(AppRepository) {
        getProfileDetailCall()
    }

    fun logout() = with(AppRepository)
    {
        logoutCall()
    }


}