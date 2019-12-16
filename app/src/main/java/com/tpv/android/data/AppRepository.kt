package com.tpv.android.data


import androidx.lifecycle.LiveData
import com.livinglifetechway.k4kotlin.core.orZero
import com.tpv.android.helper.Pref
import com.tpv.android.model.network.*
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
                    result.add(utilityList.get(index).commodity + " Programs")
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

    //Save Recording,Signature
    fun CoroutineScope.saveMediaCall(leadId: RequestBody, mediaFile: MultipartBody.Part) = dataApi<Any?, APIError> {
        fromNetwork {
            ApiClient.service.saveMedia(leadid = leadId, mediaFile = mediaFile).getResult().map { it?.data }
        }
    }

    //Save Contract
    fun CoroutineScope.saveContractCall(contractReq: ContractReq) = dataApi<Any?, APIError> {
        fromNetwork {
            ApiClient.service.sendContract(contractReq).getResult().map { it?.data }
        }
    }

    //Verification (email,Phone)
    fun CoroutineScope.selfVerificationCall(successReq: SuccessReq) = dataApi<Any?, APIError> {
        fromNetwork {
            ApiClient.service.selfVerify(successReq).getResult().map { it?.data }
        }
    }


    //Generate OTP
    fun CoroutineScope.generateOTPCall(otpReq: OTPReq) = dataApi<Any?, APIError> {
        fromNetwork {
            ApiClient.service.sendOtp(otpReq).getResult().map { it?.data }
        }
    }

    //Verify OTP
    fun CoroutineScope.verifyOTPCall(verifyOTPReq: VerifyOTPReq) = dataApi<Any?, APIError> {
        fromNetwork {
            ApiClient.service.verifyOtp(verifyOTPReq).getResult().map { it?.data }
        }
    }

    //ForgotPassword
    fun CoroutineScope.forgotPasswordCall(forgotPasswordReq: ForgotPasswordReq) = dataApi<CommonResponse<Any>?, APIError> {
        fromNetwork {
            ApiClient.service.forgotPassword(forgotPasswordReq).getResult().map { it }
        }
    }

    fun CoroutineScope.getLeadDetailCall(leadId: String?) = dataApi<LinkedHashMap<String?, String?>?, APIError> {
        fromNetwork {
            ApiClient.service.getLeadDetail(leadId).getResult().map { it?.data }
        }
    }

    fun CoroutineScope.getDynamicFormCall(dynamicFormReq: DynamicFormReq) = dataApi<List<DynamicFormResp>?, APIError> {
        fromNetwork {
            ApiClient.service.getDynamicForm(dynamicFormReq).getResult().map { it?.data }
        }
    }
}