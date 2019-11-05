package com.tpv.android.data

import com.tpv.android.helper.UserPref
import com.tpv.android.model.*
import com.tpv.android.network.ApiClient
import com.tpv.android.network.resources.APIError
import com.tpv.android.network.resources.dataApi
import com.tpv.android.network.resources.getResult
import com.tpv.android.network.resources.map
import kotlinx.coroutines.CoroutineScope


object AppRepository {

    fun CoroutineScope.logIn(loginReq: LoginReq) = dataApi<LogInResp?, APIError> {
        fromNetwork {
            ApiClient.service.logIn(loginReq).getResult().map {
                val logInResp = it?.data
                UserPref.token = logInResp?.token
                logInResp
            }
        }
    }

    fun CoroutineScope.getDashBoard() = dataApi<List<Dashboard>, APIError> {
        fromNetwork {
            ApiClient.service.getDashboardDetail().getResult().map { it?.data.orEmpty() }
        }
    }


    fun CoroutineScope.getProfileDetail() = dataApi<UserDetail?, APIError> {
        fromNetwork {
            ApiClient.service.getProfile().getResult().map { it?.data }
        }
    }


    suspend fun getLeads(leadReq: LeadReq) = ApiClient.service.getMyLeadList(leadReq).getResult()

}