package com.tpv.android.ui.home.enrollment.planszipcode

import com.tpv.android.data.AppRepository
import com.tpv.android.model.UtilityReq
import com.tpv.android.model.ZipCodeReq
import com.tpv.android.network.resources.CoroutineScopedViewModel

class PlansZipcodeViewModel : CoroutineScopedViewModel() {

//    private val zipCodeResourceMutableLiveData = MutableLiveData<Resource<List<ZipCodeResp>, APIError>>()
//    val zipCodeResourceLiveData = zipCodeResourceMutableLiveData.asLiveData()
//    val zipCodeLiveData: LiveData<List<ZipCodeResp>?> = Transformations.map(zipCodeResourceLiveData) { it?.data }

    fun getZipCode(zipCodeReq: ZipCodeReq) = with(AppRepository)
    {
        getZipCodeCall(zipCodeReq)
    }

    fun getUtility(utilityReq: UtilityReq) = with(AppRepository)
    {
        getUtilityCall(utilityReq)
    }

}