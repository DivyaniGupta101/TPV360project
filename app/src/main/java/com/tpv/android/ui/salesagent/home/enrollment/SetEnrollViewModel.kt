package com.tpv.android.ui.salesagent.home.enrollment

import android.graphics.Bitmap
import android.location.Location
import android.util.Log
import com.tpv.android.data.AppRepository
import com.tpv.android.model.network.*
import com.tpv.android.network.resources.CoroutineScopedViewModel
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.ui.salesagent.home.enrollment.dynamicform.DynamicFormFragment
import com.tpv.android.utils.enums.DynamicField
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class SetEnrollViewModel : CoroutineScopedViewModel() {

    var planId: String = ""
    var leadvalidationbackpressed:Boolean=false

    var selectedUtilityList: ArrayList<UtilityResp> = ArrayList()
    var programList: ArrayList<ProgramsResp> = ArrayList()
    var savedLeadResp: SaveLeadsDetailResp? = null
    var recordingFile: String = ""
    var isAgreeWithCondition: Boolean = false
    var signature: Bitmap? = null
    var add_enrollement:Boolean?=null
    var mList: ArrayList<String> = ArrayList()
    var templeaddetails: ArrayList<TmpDataItem> = ArrayList()
    var formPageMap: LinkedHashMap<Int, List<DynamicFormResp>>? = LinkedHashMap()
    var duplicatePageMap: LinkedHashMap<Int, List<DynamicFormResp>>? = LinkedHashMap()
    var dynamicFormData = ArrayList<DynamicFormResp>()
    var dynamicFormDatanew = ArrayList<DynamicFormResp>()
    var leadvelidationError: VelidateLeadsDetailResp? = null
    var location: Location? = null
    var utilityList: ArrayList<Commodity> = ArrayList()
    var zipcode: String = ""
    var selectedState: UtilityStateResp? = null
    var dynamicSettings: DynamicSettingResponse? = null
    var selectionType: String = ""
    var emailVerified: String = ""
    var upload_imagefile:String=""
    var file_uploaded:File?=null
    var phoneVerified: String = ""
    var selected_stateposition:String=""
    var selected_zipcode:String=""
    var multienrollementbutton:Int?=null
    var addenrollement:Boolean=false
    var list: ArrayList<DynamicFormResp> = ArrayList()
    var secondclick:Boolean?=null
    var programid:String=""
    var onbackclick:Boolean=false
    var customerback:Boolean=false
    var add_enrollement_value:Boolean?=null
    var utility_list:ArrayList<String> =ArrayList()
    var is_image_upload: Boolean?=null
    var is_image_upload_mandatory: Boolean?=null
    var gaslist: ArrayList<GasdataItem> = ArrayList()
    var electric_list: ArrayList<ElectricdataItem> = ArrayList()
    var position:Int=-1
    var first_tmp_lead:String=""
    var counter:Int=0
    var parent_id:String=""






    fun getDynamicForm(addenrollement:Boolean,dynamicFormReq: DynamicFormReq) = with(AppRepository) {

        val result = getDynamicFormCall(addenrollement,dynamicFormReq)
        result.observeForever {
            it.ifSuccess {
                Log.e("dynamicformresponse",it.toString())
                if(add_enrollement==true){
                    list.clear()
                    duplicatePageMap?.clear()
                }

                //Get total page using separate tye
                var page = 1

                val totalPage = it?.filter { it.type == DynamicField.SEPARATE.type }?.size?.plus(1)

                it?.forEachIndexed { index, dynamicFormResp ->
                    multienrollementbutton=dynamicFormResp.mutipleEnrollment


                    //Check if value page is last page then only add value don't need to check type
                    if (page != totalPage) {

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


    fun customerverificationinformation(templeadid:RequestCustomer)= with(AppRepository){
        getcustomerinformation(templeadid)
    }

    fun cancelenrollementform(templeadid: String)= with(AppRepository){
        getcancelenrollement(templeadid)
    }

    fun getimageupload(utilityid: Requentutilityid) = with(AppRepository) {
        getimagevalue(utilityid)
    }

    fun saveMedia(lng: RequestBody, lat: RequestBody, leadId: RequestBody, mediaFile: MultipartBody.Part) = with(AppRepository) {
        saveMediaCall(lat = lat, lng = lng, leadId = leadId, mediaFile = mediaFile)
    }

    fun saveBillingImage(lng: RequestBody, lat: RequestBody, leadId: RequestBody, mediaFile: MultipartBody.Part) = with(AppRepository) {
        savebilling(lat = lat, lng = lng, leadId = leadId, mediaFile = mediaFile)
    }

    fun generateOTP(otpReq: OTPReq) = with(AppRepository) {
        generateOTPCall(otpReq)
    }

    fun verifyOTP(verifyOTPReq: VerifyOTPReq) = with(AppRepository) {
        verifyOTPCall(verifyOTPReq)
    }

    fun generateenrollementtype(enrollementtype:String,clientid:String) = with(AppRepository) {
        getenrollmenttype(enrollementtype,clientid)
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

    fun gasutility(utilityid: String) = with(AppRepository) {
        getgasprograms(utilityid)
    }

    fun electricutility(utilityid: String,rewardname:String) = with(AppRepository) {
        getelectricprogram(utilityid,rewardname)
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
        upload_imagefile=""
        isAgreeWithCondition = false
        signature = null
        formPageMap = LinkedHashMap()
        duplicatePageMap = LinkedHashMap()
        utilityList.clear()
        zipcode = ""
        programid=""
        dynamicSettings = null
        selectedState = null
        phoneVerified = ""
        emailVerified = ""
        selectionType = ""
        multienrollementbutton=0
        secondclick=null
        list.clear()
        customerback=false
        duplicatePageMap?.clear()
        onbackclick=false
        add_enrollement_value=false
        utility_list.clear()
        is_image_upload=null
        is_image_upload_mandatory=null
        gaslist.clear()
        mList.clear()
        add_enrollement=null
        templeaddetails.clear()
        addenrollement=false
        electric_list.clear()
        dynamicFormData.clear()
        counter=0
        first_tmp_lead=""
        parent_id=""
        leadvalidationbackpressed=false
    }
}