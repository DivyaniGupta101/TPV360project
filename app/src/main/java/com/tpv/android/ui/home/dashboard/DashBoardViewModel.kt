package com.tpv.android.ui.home.dashboard

import com.tpv.android.data.AppRepository
import com.tpv.android.network.resources.CoroutineScopedViewModel

class DashBoardViewModel : CoroutineScopedViewModel() {

    fun getDashBoardDetail() = with(AppRepository){
        getDashBoardCall()
    }

}