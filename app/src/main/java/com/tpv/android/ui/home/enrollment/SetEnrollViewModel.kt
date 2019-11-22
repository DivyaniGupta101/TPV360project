package com.tpv.android.ui.home.enrollment

import com.tpv.android.data.AppRepository
import com.tpv.android.data.AppRepository.saveRecordingCall
import com.tpv.android.model.*
import com.tpv.android.network.resources.CoroutineScopedViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody

class SetEnrollViewModel : CoroutineScopedViewModel() {

    var planType: String = ""
    var utilitiesList: ArrayList<UtilityResp> = ArrayList()
    var zipcode: ZipCodeResp? = null
    var programList: ArrayList<ProgramsResp> = ArrayList()
    var serviceDetail: ServiceDetail = ServiceDetail()
    var savedLeadDetail: SaveLeadsDetailResp? = null




    fun saveContract(contractReq: ContractReq) = with(AppRepository) {
        saveContractCall(contractReq)
    }

    fun saveRecording(leadId: RequestBody, mediaFile: MultipartBody.Part) = with(AppRepository) {
        saveRecordingCall(leadId, mediaFile)
    }
}