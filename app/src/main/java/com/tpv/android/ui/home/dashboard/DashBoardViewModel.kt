package com.tpv.android.ui.home.dashboard

import com.tpv.android.data.AppRepository
import com.tpv.android.model.Dashboard
import com.tpv.android.network.ApiClient
import com.tpv.android.network.resources.APIError
import com.tpv.android.network.resources.CoroutineScopedViewModel
import com.tpv.android.network.resources.dataApi

class DashBoardViewModel : CoroutineScopedViewModel() {

    fun getDashBoardDetail() = with(AppRepository){
        getDashBoard()
    }

}