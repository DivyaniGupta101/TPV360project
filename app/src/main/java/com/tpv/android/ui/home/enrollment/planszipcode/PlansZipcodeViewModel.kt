package com.tpv.android.ui.home.enrollment.planszipcode

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.livinglifetechway.k4kotlin.core.orFalse
import com.tpv.android.data.AppRepository
import com.tpv.android.helper.asLiveData
import com.tpv.android.model.UtilityReq
import com.tpv.android.model.ZipCodeReq
import com.tpv.android.model.ZipCodeResp
import com.tpv.android.network.resources.CoroutineScopedViewModel
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.network.resources.extensions.ifSuccess

class PlansZipcodeViewModel : CoroutineScopedViewModel() {

    private val zipCodeMutableLiveData = MutableLiveData<List<ZipCodeResp>>()
    val zipCodeLiveData: LiveData<List<ZipCodeResp>> = zipCodeMutableLiveData.asLiveData()

    private val observer: (Resource<List<ZipCodeResp>, APIError>) -> Unit = {
        it.ifSuccess { list ->
            if (list?.isNotEmpty().orFalse()) {
                zipCodeMutableLiveData.value = list
            }
        }
    }
    private var currentZipCodeCall: LiveData<Resource<List<ZipCodeResp>, APIError>>? = null

    fun getZipCode(zipCodeReq: ZipCodeReq) {
        with(AppRepository) {
            // remove existing observer so that old api call doesn't replace existing data
            currentZipCodeCall?.removeObserver(observer)

            // request new results
            currentZipCodeCall = getZipCodeCall(zipCodeReq)

            // observe on it to get the latest data
            currentZipCodeCall?.observeForever(observer)
        }
    }

    fun getUtility(utilityReq: UtilityReq) = with(AppRepository) {
        getUtilityCall(utilityReq)
    }

}