package com.tpv.android.data

import com.tpv.android.model.Dashboard
import com.tpv.android.model.UserDetail
import com.tpv.android.network.ApiClient
import com.tpv.android.network.resources.APIError
import com.tpv.android.network.resources.dataApi
import com.tpv.android.network.resources.getResult
import com.tpv.android.network.resources.map
import kotlinx.coroutines.CoroutineScope


object AppRepository {

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

}