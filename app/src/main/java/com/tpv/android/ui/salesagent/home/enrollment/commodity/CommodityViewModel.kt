package com.tpv.android.ui.salesagent.home.enrollment.commodity

import com.tpv.android.data.AppRepository
import com.tpv.android.network.resources.CoroutineScopedViewModel

class CommodityViewModel : CoroutineScopedViewModel() {

    fun getCommodity() = with(AppRepository) {
        getCommodityCall()
    }

}