package com.tpv.android.ui.home.dashboard

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tpv.android.R
import com.tpv.android.data.AppRepository
import com.tpv.android.model.DashBoardItem
import com.tpv.android.network.resources.CoroutineScopedViewModel
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.utils.enums.LeadStatus

class DashBoardViewModel : CoroutineScopedViewModel() {

    private val dashBoardCountMutableLiveData = MutableLiveData<ArrayList<DashBoardItem>>()
    val dashBoardCount: LiveData<ArrayList<DashBoardItem>> = dashBoardCountMutableLiveData

    fun getDashBoardDetail(context: Context) = with(AppRepository) {
        val dashBoardStatusCount = arrayListOf(
                DashBoardItem(context.getDrawable(R.drawable.ic_dashboard_pending), context.getString(R.string.pending_leads), "-", LeadStatus.PENDING.value),
                DashBoardItem(context.getDrawable(R.drawable.ic_dashboard_verified), context.getString(R.string.verified_leads), "-", LeadStatus.VERIFIED.value),
                DashBoardItem(context.getDrawable(R.drawable.ic_dashboard_declined), context.getString(R.string.declined_leads), "-", LeadStatus.DECLINED.value),
                DashBoardItem(context.getDrawable(R.drawable.ic_dashboard_disconnected), context.getString(R.string.disconnected_calls), "-", LeadStatus.DISCONNECTED.value)
        )
        getDashBoardCall().observeForever {

            it.ifSuccess {

                it?.forEach { dashboard ->
                    dashBoardStatusCount.find { it.statusType == dashboard.status }?.apply {
                        statusCount = dashboard.value.toString()

                    }
                }
                dashBoardCountMutableLiveData.value = dashBoardStatusCount
            }
        }
        dashBoardCountMutableLiveData.value = dashBoardStatusCount
    }

}