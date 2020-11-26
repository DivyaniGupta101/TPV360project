package com.tpv.android.ui.salesagent.home.enrollment

import android.graphics.Bitmap
import android.location.Location
import com.tpv.android.data.AppRepository
import com.tpv.android.model.network.*
import com.tpv.android.network.resources.CoroutineScopedViewModel
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.utils.enums.DynamicField
import okhttp3.MultipartBody
import okhttp3.RequestBody

class SetEnrollViewModel : CoroutineScopedViewModel() {

    var planId: String = ""
    var selectedUtilityList: ArrayList<UtilityResp> = ArrayList()
    var programList: ArrayList<ProgramsResp> = ArrayList()
    var savedLeadResp: SaveLeadsDetailResp? = null
    var recordingFile: String = ""
    var isAgreeWithCondition: Boolean = false
    var signature: Bitmap? = null
    var formPageMap: LinkedHashMap<Int, List<DynamicFormResp>>? = LinkedHashMap()
    var duplicatePageMap: LinkedHashMap<Int, List<DynamicFormResp>>? = LinkedHashMap()
    var dynamicFormData = ArrayList<DynamicFormResp>()
    var leadvelidationError: VelidateLeadsDetailResp? = null
    var location: Location? = null
    var utilityList: ArrayList<Commodity> = ArrayList()
    var zipcode: String = ""
    var selectedState: UtilityStateResp? = null
    var dynamicSettings: DynamicSettingResponse? = null
    var selectionType: String = ""
    var emailVerified: String = ""
    var phoneVerified: String = ""

    fun getDynamicForm(dynamicFormReq: DynamicFormReq) = with(AppRepository) {
        val result = getDynamicFormCall(dynamicFormReq)

        result.observeForever {
            it.ifSuccess {

                var list: ArrayList<DynamicFormResp> = ArrayList()

                //Get total page using separate tye
                var page = 1
                val totalPage = it?.filter { it.type == DynamicField.SEPARATE.type }?.size?.plus(1)

                it?.forEachIndexed { index, dynamicFormResp ->

                    //Check if value page is last page then only add value don't need to check type
                    if (page != totalPage) {

                        //Mapping using page number as key and value as data between two separate type

                        if (dynamicFormResp.type == DynamicField.SEPARATE.type) {
                            duplicatePageMap?.put(page, list)
                            page += 1
                            list = ArrayList()
                        } else {
                            list.add(dynamicFormResp)
                        }

                    } else {

                        list.add(dynamicFormResp)
                        if (index == it.size.minus(1)) {
                            duplicatePageMap?.put(page, list)
                        }

                    }
                }
            }
        }
        result
    }

    fun saveLeadDetail(leadsDetailReq: SaveLeadsDetailReq) = with(AppRepository)
    {
        saveLeadDetailCall(leadsDetailReq)
    }

    fun validateLeadDetail(validateDetailReq: ValidateLeadsDetailReq) = with(AppRepository)
    {
        validateLeadDetailCall(validateDetailReq)
    }

    fun cancelLeadDetail(id: String, cancelLeadReq: CancelLeadReq) = with(AppRepository)
    {
        cancelLeadCall(id, cancelLeadReq)
    }


    fun saveMedia(lng: RequestBody, lat: RequestBody, leadId: RequestBody, mediaFile: MultipartBody.Part) = with(AppRepository) {
        saveMediaCall(lat = lat, lng = lng, leadId = leadId, mediaFile = mediaFile)
    }

    fun generateOTP(otpReq: OTPReq) = with(AppRepository) {
        generateOTPCall(otpReq)
    }

    fun verifyOTP(verifyOTPReq: VerifyOTPReq) = with(AppRepository) {
        verifyOTPCall(verifyOTPReq)
    }

    fun generateEmailOTP(otpEmailReq: OTPEmailReq) = with(AppRepository) {
        generateEmailOTPCall(otpEmailReq)
    }

    fun verifyEmailOTP(verifyOTPEmailReq: VerifyOTPEmailReq) = with(AppRepository) {
        verifyOTPEmailCall(verifyOTPEmailReq)
    }

    fun getPrograms(utilityList: ArrayList<UtilityResp>) = with(AppRepository) {
        getProgramsCall(utilityList)
    }

    fun selfVerification(successReq: SuccessReq) = with(AppRepository) {
        selfVerificationCall(successReq)
    }

    fun setTPVCallData(scheduleTPVCallRequest: ScheduleTPVCallRequest) = with(AppRepository) {
        setTPVCall(scheduleTPVCallRequest)
    }

    /**
     * Remove stored values in viewModel
     */
    fun clearSavedData() {
        selectedUtilityList.clear()
        planId = ""
        programList.clear()
        savedLeadResp = null
        recordingFile = ""
        isAgreeWithCondition = false
        signature = null
        formPageMap = LinkedHashMap()
        duplicatePageMap = LinkedHashMap()
        utilityList.clear()
        zipcode = ""
        dynamicSettings = null
        selectedState = null
        phoneVerified = ""
        emailVerified = ""
        selectionType = ""
    }
}