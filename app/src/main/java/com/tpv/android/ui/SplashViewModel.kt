package com.tpv.android.ui

import com.tpv.android.data.AppRepository
import com.tpv.android.model.network.ForceUpdateReq
import com.tpv.android.network.resources.CoroutineScopedViewModel


class SplashViewModel : CoroutineScopedViewModel() {

    fun forceUpdate(forceUpdateReq: ForceUpdateReq) = with(AppRepository)
    {
        forceUpdateCall(forceUpdateReq)
    }
}