package com.tpv.android.ui.salesagent.home.enrollment.planszipcode

import androidx.lifecycle.MutableLiveData
import com.tpv.android.data.AppRepository
import com.tpv.android.model.network.DynamicSettingsReq
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

    fun getEnrollWithState(dynamicSettingsReq: DynamicSettingsReq) = with(AppRepository) {
        getEnrollWithStateCall(dynamicSettingsReq)
    }
    fun getUtilityState(dynamicSettingsReq: DynamicSettingsReq) = with(AppRepository) {
        getUtilityStateCall(dynamicSettingsReq)
    }

    fun clearZipCodeListData() {
        currentZipCodeCallMutableLiveData.value = Resource.empty()
    }

}