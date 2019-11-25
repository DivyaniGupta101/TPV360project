package com.tpv.android.ui.home.enrollment

import com.tpv.android.data.AppRepository
import com.tpv.android.model.*
import com.tpv.android.network.resources.CoroutineScopedViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody

class SetEnrollViewModel : CoroutineScopedViewModel() {

    var planType: String = ""
    var utilitiesList: ArrayList<UtilityResp> = ArrayList()
    var zipcode: ZipCodeResp? = null
    var programList: ArrayList<ProgramsResp> = ArrayList()
    var customerData: CustomerData = CustomerData()
    var savedLeadDetail: SaveLeadsDetailResp? = null
    var recordingFile: String = ""
    var isElectricServiceAddressSame: Boolean? = false
    var isGasServiceAddressSame: Boolean? = false


    fun saveLeadDetail(leadsDetailReq: SaveLeadsDetailReq) = with(AppRepository)
    {
        saveLeadDetailCall(leadsDetailReq)
    }

    fun saveContract(contractReq: ContractReq) = with(AppRepository) {
        saveContractCall(contractReq)
    }

    fun saveRecording(leadId: RequestBody, mediaFile: MultipartBody.Part) = with(AppRepository) {
        saveRecordingCall(leadId, mediaFile)
    }

    fun generateOTP(otpReq: OTPReq) = with(AppRepository) {
        generateOTPCall(otpReq)
    }

    fun verifyOTP(verifyOTPReq: VerifyOTPReq) = with(AppRepository) {
        verifyOTPCall(verifyOTPReq)
    }

    fun getPrograms(utilityList: ArrayList<UtilityResp>) = with(AppRepository)
    {
        getProgramsCall(utilityList)
    }

    fun selfVerification(successReq: SuccessReq) = with(AppRepository) {
        selfVerificationCall(successReq)
    }
}