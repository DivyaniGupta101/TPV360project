package com.tpv.android.ui.home.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.livinglifetechway.k4kotlin.core.orFalse
import com.tpv.android.data.AppRepository
import com.tpv.android.model.DashBoardStatusCount
import com.tpv.android.network.resources.CoroutineScopedViewModel
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.utils.LeadStatus

class DashBoardViewModel : CoroutineScopedViewModel() {

    private val dashBoardCountMutableLiveData = MutableLiveData<DashBoardStatusCount>()

    val dashBoardCount: LiveData<DashBoardStatusCount> = dashBoardCountMutableLiveData

    init {
        getDashBoardDetail()
    }

    fun getDashBoardDetail() = with(AppRepository) {
        val dashBoardStatusCount = DashBoardStatusCount()
        getDashBoardCall().observeForever {

            it.ifSuccess {

                it?.forEach { dashboard ->
                    if (dashboard.status?.equals(LeadStatus.PENDING.value).orFalse()) {
                        dashBoardStatusCount.pending = dashboard.value.toString()
                    }
                    if (dashboard.status?.equals(LeadStatus.VERIFIED.value).orFalse()) {
                        dashBoardStatusCount.verified = dashboard.value.toString()
                    }
                    if (dashboard.status?.equals(LeadStatus.DECLINED.value).orFalse()) {
                        dashBoardStatusCount.declined = dashboard.value.toString()
                    }
                    if (dashboard.status?.equals(LeadStatus.HANGUP.value).orFalse()) {
                        dashBoardStatusCount.hangUp = dashboard.value.toString()
                    }
                }
                dashBoardCountMutableLiveData.value = dashBoardStatusCount

            }
        }
        dashBoardCountMutableLiveData.value = dashBoardStatusCount
    }

}