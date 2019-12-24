package com.tpv.android.ui.home.enrollment

import android.graphics.Bitmap
import com.tpv.android.data.AppRepository
import com.tpv.android.model.network.*
import com.tpv.android.network.resources.CoroutineScopedViewModel
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.utils.enums.DynamicField
import okhttp3.MultipartBody
import okhttp3.RequestBody

class SetEnrollViewModel : CoroutineScopedViewModel() {

    var planType: String = ""
    var utilitiesList: ArrayList<UtilityResp> = ArrayList()
    var programList: ArrayList<ProgramsResp> = ArrayList()
    var savedLeadResp: SaveLeadsDetailResp? = null
    var recordingFile: String = ""
    var isAgreeWithCondition: Boolean = false
    var signature: Bitmap? = null
    var formPageMap: LinkedHashMap<Int, List<DynamicFormResp>>? = LinkedHashMap()
    var dynamicFormData = ArrayList<DynamicFormResp>()

    fun getDynamicForm(dynamicFormReq: DynamicFormReq) = with(AppRepository) {
        val result = getDynamicFormCall(dynamicFormReq)

        result.observeForever {
            it.ifSuccess {

                var list: ArrayList<DynamicFormResp> = ArrayList()
                var page = 1
                val totalPage = it?.filter { it.type == DynamicField.SEPARATE.type }?.size?.plus(1)

                it?.forEachIndexed { index, dynamicFormResp ->

                    if (page != totalPage) {

                        if (dynamicFormResp.type == DynamicField.SEPARATE.type) {
                            formPageMap?.put(page, list)
                            page += 1
                            list = ArrayList()
                        } else {
                            list.add(dynamicFormResp)
                        }

                    } else {

                        list.add(dynamicFormResp)
                        if (index == it.size.minus(1)) {
                            formPageMap?.put(page, list)
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
        programList.clear()
        savedLeadResp = null
        recordingFile = ""
        isAgreeWithCondition = false
        signature = null
        formPageMap = LinkedHashMap()
    }
}