package com.tpv.android.ui.home

import com.tpv.android.data.AppRepository

class HomeViewModel : com.tpv.android.network.resources.CoroutineScopedViewModel() {

    fun getProfile() = with(AppRepository) {
        getProfileDetail()
    }

}