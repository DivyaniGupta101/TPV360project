package com.tpv.android.data

import androidx.lifecycle.LiveData
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

    fun CoroutineScope.getDashBoardCall() = dataApi<List<DashboardResp>, APIError> {
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
            ApiClient.service.getMyLeadList(leadReq).getResult().map { it?.data.orEmpty() }
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

}