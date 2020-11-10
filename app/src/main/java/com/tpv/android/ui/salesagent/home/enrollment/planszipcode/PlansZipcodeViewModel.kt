package com.tpv.android.ui.salesagent.home.enrollment.planszipcode

import androidx.lifecycle.MutableLiveData
import com.tpv.android.data.AppRepository
import com.tpv.android.model.network.EnrollWithStateReq
import com.tpv.android.model.network.UtilityReq
import com.tpv.android.model.network.ZipCodeReq
import com.tpv.android.model.network.ZipCodeResp
import com.tpv.android.network.resources.CoroutineScopedViewModel
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.network.resources.dataproviders.mapToResource

class PlansZipcodeViewModel : CoroutineScopedViewModel() {


    private var currentZipCodeCallMutableLiveData = MutableLiveData<Resource<List<ZipCodeResp>, APIError>>()


    fun getZipCodeSynchronously(zipCodeReq: ZipCodeReq) =
            AppRepository.getZipCodeCall(zipCodeReq).mapToResource()


    fun getUtility(utilityReq: UtilityReq) = with(AppRepository) {
        getUtilityCall(utilityReq)
    }

    fun getEnrollWithState(enrollWithStateReq: EnrollWithStateReq) = with(AppRepository) {
        getEnrollWithStateCall(enrollWithStateReq)
    }
    fun getUtilityState(enrollWithStateReq: EnrollWithStateReq) = with(AppRepository) {
        getUtilityStateCall(enrollWithStateReq)
    }

    fun clearZipCodeListData() {
        currentZipCodeCallMutableLiveData.value = Resource.empty()
    }

}