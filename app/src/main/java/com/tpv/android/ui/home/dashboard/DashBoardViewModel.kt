package com.tpv.android.ui.home.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.livinglifetechway.k4kotlin.core.orFalse
import com.tpv.android.data.AppRepository
import com.tpv.android.model.DashBoardStatusCount
import com.tpv.android.network.resources.CoroutineScopedViewModel
import com.tpv.android.utils.LeadStatus

class DashBoardViewModel : CoroutineScopedViewModel() {

    private val dashBoardCountMutableLiveData = MutableLiveData<DashBoardStatusCount>()

    val dashBoardCount: LiveData<DashBoardStatusCount> = dashBoardCountMutableLiveData

    init {
        getDashBoardDetail()
    }

    fun getDashBoardDetail() = with(AppRepository) {
        getDashBoardCall().observeForever {
            val dashBoardStatusCount = DashBoardStatusCount()

            it?.data?.forEach { dashboard ->
                if (dashboard.status?.equals(LeadStatus.PENDING.value).orFalse()) {
                    dashBoardStatusCount.apply { pending = dashboard.value.toString() }
                }
                if (dashboard.status?.equals(LeadStatus.VERIFIED.value).orFalse()) {
                    dashBoardStatusCount.apply { verified = dashboard.value.toString() }
                }
                if (dashboard.status?.equals(LeadStatus.DECLINED.value).orFalse()) {
                    dashBoardStatusCount.apply { decliend = dashboard.value.toString() }
                }
                if (dashboard.status?.equals(LeadStatus.HANGUP.value).orFalse()) {
                    dashBoardStatusCount.apply { hangUp = dashboard.value.toString() }
                }
            }
            dashBoardCountMutableLiveData.value = dashBoardStatusCount

        }
    }

}