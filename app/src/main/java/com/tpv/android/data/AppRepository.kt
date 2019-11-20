package com.tpv.android.data

import androidx.lifecycle.LiveData
import com.livinglifetechway.k4kotlin.core.orZero
import com.tpv.android.helper.Pref
import com.tpv.android.model.*
import com.tpv.android.network.ApiClient
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.network.resources.dataproviders.dataApi
import com.tpv.android.network.resources.dataproviders.paginatedDataApi
import com.tpv.android.network.resources.getResult
import com.tpv.android.network.resources.map
import kotlinx.coroutines.CoroutineScope
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody


object AppRepository {

    fun CoroutineScope.logInCall(loginReq: LoginReq) = dataApi<LoginResp?, APIError> {
        fromNetwork {
            ApiClient.service.logIn(loginReq).getResult().map {
                val logInResp = it?.data
                Pref.token = it?.token
                logInResp
            }
        }
    }

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


    fun CoroutineScope.getProgramsCall(programsReq: ProgramsReq) = dataApi<List<ProgramsResp>, APIError> {
        fromNetwork {
            ApiClient.service.getPrograms(programsReq).getResult().map { it?.data.orEmpty() }
        }
    }

    fun CoroutineScope.saveLeadDetailCall(saveLeadsDetailReq: SaveLeadsDetailReq) = dataApi<SaveLeadsDetailResp?, APIError> {
        fromNetwork {
            ApiClient.service.saveLeadDetail(saveLeadsDetailReq).getResult().map { it?.data }
        }
    }

    fun CoroutineScope.saveRecordingcall(leadId: RequestBody, mediaFile: MultipartBody.Part) = dataApi<RecordingResp?, APIError> {
        fromNetwork {
            ApiClient.service.saveRecording(leadid = leadId, mediaFile = mediaFile).getResult().map { it?.data }
        }
    }

}