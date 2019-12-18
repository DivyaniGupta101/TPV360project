package com.tpv.android.ui.home.enrollment

import android.graphics.Bitmap
import com.tpv.android.data.AppRepository
import com.tpv.android.model.network.*
import com.tpv.android.network.resources.CoroutineScopedViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody

class SetEnrollViewModel : CoroutineScopedViewModel() {

    var planType: String = ""
    var utilitiesList: ArrayList<UtilityResp> = ArrayList()
    var zipcode: ZipCodeResp? = null
    var programList: ArrayList<ProgramsResp> = ArrayList()
    var relationShipList: ArrayList<String> = ArrayList()
    var customerData: CustomerData = CustomerData()
    var savedLeadDetail: SaveLeadsDetailResp? = null
    var recordingFile: String = ""
    var isElectricServiceAddressSame: Boolean? = false
    var customerDataList: ArrayList<CustomerData?> = ArrayList()
    var isAgreeWithCondition: Boolean = false
    var signature: Bitmap? = null
    var dynamicFormCurrentPage: Int = 1
    var dynamicForm: LinkedHashMap<Int, List<DynamicFormResp>>? = LinkedHashMap()


    fun getDynamicForm(dynamicFormReq: DynamicFormReq) = with(AppRepository) {
        getDynamicFormCall(dynamicFormReq)
    }

    fun saveLeadDetail(leadsDetailReq: SaveLeadsDetailReq) = with(AppRepository)
    {
        saveLeadDetailCall(leadsDetailReq)
    }

    fun saveContract(contractReq: ContractReq) = with(AppRepository) {
        saveContractCall(contractReq)
    }

    fun saveMedia(leadId: RequestBody, mediaFile: MultipartBody.Part) = with(AppRepository) {
        saveMediaCall(leadId, mediaFile)
    }

    fun generateOTP(otpReq: OTPReq) = with(AppRepository) {
        generateOTPCall(otpReq)
    }

    fun verifyOTP(verifyOTPReq: VerifyOTPReq) = with(AppRepository) {
        verifyOTPCall(verifyOTPReq)
    }

    fun getPrograms(utilityList: ArrayList<UtilityResp>) = with(AppRepository) {
        getProgramsCall(utilityList)
    }

    fun selfVerification(successReq: SuccessReq) = with(AppRepository) {
        selfVerificationCall(successReq)
    }

    /**
     * Remove stored values in viewModel
     */
    fun clearSavedData() {
        utilitiesList.clear()
        planType = ""
        zipcode = null
        programList.clear()
        customerData = CustomerData()
        savedLeadDetail = null
        recordingFile = ""
        customerDataList.clear()
        isElectricServiceAddressSame = false
        relationShipList.clear()
        isAgreeWithCondition = false
        signature = null
        dynamicFormCurrentPage = 1
    }
}