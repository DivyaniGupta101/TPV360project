package com.tpv.android.ui.home.enrollment.planszipcode

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.livinglifetechway.k4kotlin.core.orFalse
import com.tpv.android.data.AppRepository
import com.tpv.android.helper.asLiveData
import com.tpv.android.model.network.UtilityReq
import com.tpv.android.model.network.ZipCodeReq
import com.tpv.android.model.network.ZipCodeResp
import com.tpv.android.network.resources.CoroutineScopedViewModel
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.network.resources.dataproviders.mapToResource
import com.tpv.android.network.resources.extensions.ifSuccess

class PlansZipcodeViewModel : CoroutineScopedViewModel() {


    private var currentZipCodeCallMutableLiveData = MutableLiveData<Resource<List<ZipCodeResp>, APIError>>()
     var zipCodeLiveData: LiveData<Resource<List<ZipCodeResp>, APIError>> = currentZipCodeCallMutableLiveData



    fun getZipCodeSynchronously(zipCodeReq: ZipCodeReq) =
        AppRepository.getZipCodeCall(zipCodeReq).mapToResource()


    fun getUtility(utilityReq: UtilityReq) = with(AppRepository) {
        getUtilityCall(utilityReq)
    }

    fun clearZipCodeListData() {
        currentZipCodeCallMutableLiveData.value = Resource.empty()
    }

}