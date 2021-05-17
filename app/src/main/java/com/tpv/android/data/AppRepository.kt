package com.tpv.android.data


import androidx.lifecycle.LiveData
import com.livinglifetechway.k4kotlin.core.orZero
import com.tpv.android.helper.Pref
import com.tpv.android.model.network.*
import com.tpv.android.model.network.TimeZone
import com.tpv.android.network.ApiClient
import com.tpv.android.network.resources.*
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.network.resources.dataproviders.dataApi
import com.tpv.android.network.resources.dataproviders.paginatedDataApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.*


object AppRepository {

    //    Login
    fun CoroutineScope.logInCall(loginReq: LoginReq) = dataApi<UserDetail?, APIError> {
        fromNetwork {
            ApiClient.service.logIn(loginReq).getResult().map {
                Pref.token = it?.token
                Pref.user = it?.data
                Pref.dashBoardUrl = it?.data?.dashBoardURL
                it?.data
            }
        }
    }

    //    Home
    fun CoroutineScope.getDashBoardCall() = dataApi<List<DashBoard>, APIError> {
        fromNetwork {
            ApiClient.service.getDashboardDetail().getResult().map { it?.data.orEmpty() }
        }
    }

    //Profile
    fun CoroutineScope.getProfileDetailCall() = dataApi<UserDetail?, APIError> {
        fromNetwork {
            ApiClient.service.getProfile().getResult().map {
                val profileResp = it?.data
                Pref.user = profileResp
                profileResp
            }
        }
    }

    //LeadList
    fun CoroutineScope.getLeadsCall(leadList: List<LeadResp>, leadReq: LeadReq): LiveData<Resource<List<LeadResp>, APIError>> = paginatedDataApi(leadList) {
        fromNetwork {
            ApiClient.service.getMyLeadList(leadReq).getResult().map {
                shouldLoadNextPage = it?.currentPage.orZero() < it?.lastPage.orZero()
                it?.data.orEmpty()
            }
        }
    }

    //Logout
    fun CoroutineScope.logoutCall() = dataApi<Unit?, APIError> {
        fromNetwork {
            val result = ApiClient.service.logout().getResult().also {
                Pref.clear()
            }
            result.map { it?.data }
        }
    }

    //zipcodeList
    fun getZipCodeCall(zipCodeReq: ZipCodeReq) =
            ApiClient.service.zipAutoCompleteApi(zipCodeReq).getResultSync().map {
                it?.data.orEmpty()
            }


    //UtilityList
    fun CoroutineScope.getUtilityCall(utilityReq: UtilityReq) = dataApi<List<UtilityResp>, APIError>
    {
        fromNetwork {
            ApiClient.service.getUtility(utilityReq).getResult().map { it?.data.orEmpty() }
        }
    }

    //ProgramsList
    fun CoroutineScope.getProgramsCall(utilityList: ArrayList<UtilityResp>) = dataApi<List<Any>, APIError>
    {
        fromNetwork {
            val deferredResultList = arrayListOf<Deferred<Result<CommonResponse<List<ProgramsResp>>, APIError>>>()

            // make all the API calls and store the deffered response in the array list
            utilityList.forEach {
                val result = async {
                    ApiClient.service.getPrograms(ProgramsReq(it.utid.toString())).getResult()
                }
                deferredResultList.add(result)
            }

            // this will be the result array containg all the response result
            val result = arrayListOf<Any>()

            // this list stores the response result -> success / failure
            val resultStatuses = arrayListOf<Result<*, APIError>>()


            // loop through all the deffered array and add all of them one by to the response list
            deferredResultList.forEachIndexed { index, deferred ->

                // get the deferred result
                val response = deferred.await()

                // add the labels and result
                response.map {
                    it?.data?.map {
                        it.utilityType = utilityList.get(index).commodity
                    }
                    result.add(utilityList.get(index).commodity + " Utility")
                    result.addAll(it?.data.orEmpty())
                }

                resultStatuses.add(response)
            }

            // get the final result state based on the combined state
            if (Result.allSuccess(*resultStatuses.toTypedArray())) {
                Result.success(result)
            } else {
                Result.getFailures(*resultStatuses.toTypedArray()).first() as Result<List<Any>, APIError>
            }
        }
    }

    //    SaveLead
    fun CoroutineScope.saveLeadDetailCall(saveLeadsDetailReq: SaveLeadsDetailReq) = dataApi<SaveLeadsDetailResp?, APIError> {
        fromNetwork {
            ApiClient.service.saveLeadDetail(saveLeadsDetailReq).getResult().map { it?.data }
        }
    }

    //    SaveLead
    fun CoroutineScope.validateLeadDetailCall(validateLeadsDetailReq: ValidateLeadsDetailReq) = dataApi<VelidateLeadsDetailResp?, APIError> {
        fromNetwork {
            ApiClient.service.validateLeadDetail(validateLeadsDetailReq).getResult().map { it?.data }
        }
    }

    fun CoroutineScope.cancelLeadCall(id: String, cancelLeadReq: CancelLeadReq) = dataApi<Any?, APIError> {
        fromNetwork {
            ApiClient.service.cancelEnrollLead(id, cancelLeadReq).getResult().map { it?.data }
        }
    }

    //Save Recording,Signature
    fun CoroutineScope.saveMediaCall(lng: RequestBody, lat: RequestBody, leadId: RequestBody, mediaFile: MultipartBody.Part) = dataApi<Any?, APIError> {
        fromNetwork {
            ApiClient.service.saveMedia(lat = lat, lng = lng, leadid = leadId, mediaFile = mediaFile).getResult().map { it?.data }
        }
    }

    //save billingimage
    fun CoroutineScope.savebilling(lng: RequestBody, lat: RequestBody, leadId: RequestBody, mediaFile: MultipartBody.Part) = dataApi<Any?, APIError> {
        fromNetwork {
            ApiClient.service.savebillingimage(lat = lat, lng = lng, leadid = leadId, mediaFile = mediaFile).getResult().map { it?.data }
        }
    }

    //Verification (email,Phone)
    fun CoroutineScope.selfVerificationCall(successReq: SuccessReq) = dataApi<Any?, APIError> {
        fromNetwork {
            ApiClient.service.selfVerify(successReq).getResult().map { it?.data }
        }
    }


    //Generate OTP For Phone Number
    fun CoroutineScope.generateOTPCall(otpReq: OTPReq) = dataApi<Any?, APIError> {
        fromNetwork {
            ApiClient.service.sendOtp(otpReq).getResult().map { it?.data }
        }
    }

    //Verify OTP For Phone Number
    fun CoroutineScope.verifyOTPCall(verifyOTPReq: VerifyOTPReq) = dataApi<Any?, APIError> {
        fromNetwork {
            ApiClient.service.verifyOtp(verifyOTPReq).getResult().map { it?.data }
        }
    }

    //Generate OTP For Email
    fun CoroutineScope.generateEmailOTPCall(otpEmailReq: OTPEmailReq) = dataApi<Any?, APIError> {
        fromNetwork {
            ApiClient.service.sendEmailOtp(otpEmailReq).getResult().map { it?.data }
        }
    }

    //Verify OTP For Phone Number
    fun CoroutineScope.verifyOTPEmailCall(verifyOTPEmailReq: VerifyOTPEmailReq) = dataApi<Any?, APIError> {
        fromNetwork {
            ApiClient.service.verifyEmailOtp(verifyOTPEmailReq).getResult().map { it?.data }
        }
    }

    //ForgotPassword
    fun CoroutineScope.forgotPasswordCall(forgotPasswordReq: ForgotPasswordReq) = dataApi<CommonResponse<Any>?, APIError> {
        fromNetwork {
            ApiClient.service.forgotPassword(forgotPasswordReq).getResult().map { it }
        }
    }

    fun CoroutineScope.getLeadDetailCall(leadId: String?) = dataApi<LeadDetailResp?, APIError> {
        fromNetwork {
            ApiClient.service.getLeadDetail(leadId).getResult().map { it?.data }
        }
    }

    fun CoroutineScope.getDynamicFormCall(dynamicFormReq: DynamicFormReq) = dataApi<List<DynamicFormResp>?, APIError> {
        fromNetwork {
            ApiClient.service.getDynamicForm(dynamicFormReq).getResult().map { it?.data }
        }
    }

    fun CoroutineScope.getCommodityCall() = dataApi<List<CommodityResp>, APIError> {
        fromNetwork {
            ApiClient.service.getCommodity(Pref.user?.clientId.toString()).getResult().map { it?.data.orEmpty() }
        }
    }

    fun CoroutineScope.getTicketCall(ticketReq: TicketReq) = dataApi<Any, APIError> {
        fromNetwork {
            ApiClient.service.getTickets(ticketReq = ticketReq).getResult()
        }
    }

    fun CoroutineScope.getCurrentActivityCall() = dataApi<CurrentActivityResponse?, APIError> {
        fromNetwork {
            ApiClient.service.getCurrentActivity().getResult().map { it?.data }
        }
    }

    fun CoroutineScope.setAgentActivityCall(agentActivityRequest: AgentActivityRequest) = dataApi<Any?, APIError> {
        fromNetwork {
            ApiClient.service.setAgentActivity(agentActivityRequest).getResult().map { it }
        }
    }

    fun CoroutineScope.setLocationCall(agentLocationRequest: AgentLocationRequest) = dataApi<Any?, APIError> {
        fromNetwork {
            ApiClient.service.setLocation(agentLocationRequest).getResult().map { it }
        }
    }

    fun CoroutineScope.setTPVCall(scheduleTPVCallRequest: ScheduleTPVCallRequest) = dataApi<CommonResponse<Any>?, APIError> {
        fromNetwork {
            ApiClient.service.setTPVCall(scheduleTPVCallRequest).getResult().map { it }
        }
    }

    fun CoroutineScope.getCriticalAlertReportListCall(alertReportList: List<ClientReportResp>, clientReportReq: ClientReportReq): LiveData<Resource<List<ClientReportResp>, APIError>> = paginatedDataApi(alertReportList) {
        fromNetwork {
            ApiClient.service.getCriticalAlertReportList(clientReportReq).getResult().map {
                shouldLoadNextPage = it?.currentPage.orZero() < it?.lastPage.orZero()
                it?.data.orEmpty()
            }
        }
    }

    fun CoroutineScope.getClientsCall() = dataApi<List<ClientsResp>, APIError> {
        fromNetwork {
            ApiClient.service.getClients().getResult().map { it?.data.orEmpty() }
        }
    }

    fun CoroutineScope.getSalesCenterCall(clientId: String?) = dataApi<List<ClientsResp>, APIError> {
        fromNetwork {
            ApiClient.service.getSalesCenter(clientId).getResult().map { it?.data.orEmpty() }
        }
    }

    fun CoroutineScope.getTimeLineCall(id: String) = dataApi<List<ClientTimeLineResp>, APIError> {
        fromNetwork {
            ApiClient.service.getTimeLine(id = id).getResult().map { it?.data.orEmpty() }
        }
    }

    fun CoroutineScope.getClientLeadDetailsCall(leadId: String) = dataApi<ClientLeadDetailResp?, APIError>
    {
        fromNetwork {
            ApiClient.service.getClientLeadDetails(leadId).getResult().map { it?.data }
        }
    }

    fun CoroutineScope.forceUpdateCall(forceUpdateReq: ForceUpdateReq) = dataApi<CommonResponse<Any>?, APIError> {
        fromNetwork {
            ApiClient.service.forceUpdate(forceUpdateReq).getResult().map { it }
        }
    }

    fun CoroutineScope.getAccountNumberRegexCall(accountNumberRegexRequest: AccountNumberRegexRequest) = dataApi<CommonResponse<List<AccountNumberRegexResp>>?, APIError> {
        fromNetwork {
            ApiClient.service.getAccountNumberRegex(accountNumberRegexRequest).getResult().map { it }
        }
    }

    fun CoroutineScope.getTimeZoneCall() = dataApi<CommonResponse<List<TimeZone>>?, APIError> {
        fromNetwork {
            ApiClient.service.getTimeZone().getResult().map { it }
        }
    }

    fun CoroutineScope.updateTimeZoneCall(timeZoneReq: TimeZoneReq) = dataApi<UserDetail?, APIError> {
        fromNetwork {
            ApiClient.service.updateTimeZone(timeZoneReq).getResult().map {
                Pref.user = it?.data
                it?.data
            }
        }
    }

    //update Profile
    fun CoroutineScope.updateProfilePhotoCall(file: MultipartBody.Part) = dataApi<UserDetail?, APIError> {
        fromNetwork {
            ApiClient.service.updateProfilePhoto(file = file).getResult().map { it?.data }
        }
    }

    //send Signature Link
    fun CoroutineScope.sendSignatureCall(sendSignatureLinkReq: SendSignatureLinkReq) = dataApi<CommonResponse<Any>?, APIError> {
        fromNetwork {
            ApiClient.service.sendSignatureLink(sendSignatureLinkReq).getResultSync().map { it }
        }
    }

    //verify Signature Link
    fun CoroutineScope.verifySignatureCall(verifySignatureReq: VerifySignatureReq) = dataApi<VerifySignatureResponse?, APIError> {
        fromNetwork {
            ApiClient.service.verifySignature(verifySignatureReq).getResult().map { it?.data }
        }
    }

    //cancel Lead
    fun CoroutineScope.cancelEnrollLeadCall(tempId: String, cancelLeadReq: CancelLeadReq) = dataApi<CommonResponse<Any>?, APIError> {
        fromNetwork {
            ApiClient.service.cancelEnrollLead(tempId, cancelLeadReq).getResult().map { it }
        }
    }

    //get status of enrollment with state
    fun CoroutineScope.getEnrollWithStateCall(dynamicSettingsReq: DynamicSettingsReq) = dataApi<DynamicSettingResponse?, APIError> {
        fromNetwork {
            ApiClient.service.getEnrollWithState(dynamicSettingsReq).getResult().map { it?.data }
        }
    }

    //get state list
    fun CoroutineScope.getUtilityStateCall(dynamicSettingsReq: DynamicSettingsReq) = dataApi<List<UtilityStateResp>?, APIError> {
        fromNetwork {
            ApiClient.service.getUtilityState(dynamicSettingsReq).getResult().map { it?.data }
        }
    }

}