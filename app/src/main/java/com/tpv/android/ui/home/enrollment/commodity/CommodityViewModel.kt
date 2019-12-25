package com.tpv.android.ui.home.enrollment.commodity

import com.tpv.android.data.AppRepository
import com.tpv.android.network.resources.CoroutineScopedViewModel

class CommodityViewModel : CoroutineScopedViewModel() {
//    private val commodityResourceMutableLiveData = MutableLiveData<Resource<List<CommodityResp>, APIError>>()
//    val commodityResourceLiveData = commodityResourceMutableLiveData.asLiveData()
//    val leadsLiveData: LiveData<List<CommodityResp>> = Transformations.map(commodityResourceLiveData) { it.data }

//    init {
//        getCommodity()
//    }
////
    fun getCommodity() = with(AppRepository) {
        getCommodityCall()
    }

}