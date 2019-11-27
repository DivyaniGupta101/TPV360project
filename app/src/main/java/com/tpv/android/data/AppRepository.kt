package com.tpv.android.data

import androidx.lifecycle.LiveData
import com.livinglifetechway.k4kotlin.core.orZero
import com.tpv.android.helper.Pref
import com.tpv.android.model.*
import com.tpv.android.network.ApiClient
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.Result
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.network.resources.dataproviders.dataApi
import com.tpv.android.network.resources.dataproviders.paginatedDataApi
import com.tpv.android.network.resources.getResult
import com.tpv.android.network.resources.map
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import okhttp3.MultipartBody
import okhttp3.RequestBody


object AppRepository {

    //    Login
    fun CoroutineScope.logInCall(loginReq: LoginReq) = dataApi<LoginResp?, APIError> {
        fromNetwork {
            ApiClient.service.logIn(loginReq).getResult().map {
                val logInResp = it?.data
                Pref.token = it?.token
                logInResp
            }
        }
    }

    //    Home
    fun CoroutineScope.getDashBoardCall() = dataApi<List<Dashboard>, APIError> {
        fromNetwork {
            ApiClient.service.getDashboardDetail().getResult().map { it?.data.orEmpty() }
        }
    }


    fun CoroutineScope.getProfileDetailCall() = dataApi<UserDetail?, APIError> {
        fromNetwork {
            ApiClient.service.getProfile().getResult().map {
                val profileResp = it?.data
                Pref.user = profileResp
                profileResp
            }
        }
    }


    fun CoroutineScope.getLeadsCall(leadList: List<LeadResp>, leadReq: LeadReq): LiveData<Resource<List<LeadResp>, APIError>> = paginatedDataApi(leadList) {
        fromNetwork {
            ApiClient.service.getMyLeadList(leadReq).getResult().map {
                shouldLoadNextPage = it?.currentPage.orZero() < it?.lastPage.orZero()
                it?.data.orEmpty()
            }
        }
    }


    fun CoroutineScope.logoutCall() = dataApi<Unit?, APIError> {
        fromNetwork {
            ApiClient.service.logout().getResult().map {
                val result = it?.data
                Pref.clear()
                result
            }
        }
    }

    //zipcode
    fun CoroutineScope.getZipCodeCall(zipCodeReq: ZipCodeReq) = dataApi<List<ZipCodeResp>, APIError> {
        fromNetwork {
            ApiClient.service.zipAutoCompleteApi(zipCodeReq).getResult().map {
                it?.data.orEmpty()
            }
        }
    }

    fun CoroutineScope.getUtilityCall(utilityReq: UtilityReq) = dataApi<List<UtilityResp>, APIError>
    {
        fromNetwork {
            ApiClient.service.getUtility(utilityReq).getResult().map { it?.data.orEmpty() }
        }
    }


    fun CoroutineScope.getProgramsCall(utilityList: ArrayList<UtilityResp>) = dataApi<List<Any>, APIError> {


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

    fun CoroutineScope.saveMediaCall(leadId: RequestBody, mediaFile: MultipartBody.Part) = dataApi<Any?, APIError> {
        fromNetwork {
            ApiClient.service.saveMedia(leadid = leadId, mediaFile = mediaFile).getResult().map { it?.data }
        }
    }


    fun CoroutineScope.saveContractCall(contractReq: ContractReq) = dataApi<Any?, APIError> {
        fromNetwork {
            ApiClient.service.sendContract(contractReq).getResult().map { it?.data }
        }
    }

    //    Sucess
    fun CoroutineScope.selfVerificationCall(successReq: SuccessReq) = dataApi<Any?, APIError> {
        fromNetwork {
            ApiClient.service.selfVerify(successReq).getResult().map { it?.data }
        }
    }


    //    OTP
    fun CoroutineScope.generateOTPCall(otpReq: OTPReq) = dataApi<Any?, APIError> {
        fromNetwork {
            ApiClient.service.sendOtp(otpReq).getResult().map { it?.data }
        }
    }

    fun CoroutineScope.verifyOTPCall(verifyOTPReq: VerifyOTPReq) = dataApi<Any?, APIError> {
        fromNetwork {
            ApiClient.service.verifyOtp(verifyOTPReq).getResult().map { it?.data }
        }
    }

}