package com.tpv.android.ui.salesagent.home.dashboard

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tpv.android.R
import com.tpv.android.data.AppRepository
import com.tpv.android.model.internal.DashBoardItem
import com.tpv.android.network.resources.CoroutineScopedViewModel
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.utils.enums.LeadStatus

class DashBoardViewModel : CoroutineScopedViewModel() {

    private val dashBoardCountMutableLiveData = MutableLiveData<ArrayList<DashBoardItem>>()
    val dashBoardCount: LiveData<ArrayList<DashBoardItem>> = dashBoardCountMutableLiveData
    var dashBoardStatusCount: ArrayList<DashBoardItem> = ArrayList()

    fun getDashBoardDetail(context: Context) = with(AppRepository) {

        getDashBoardCall().observeForever {

            it.ifSuccess {
                if (dashBoardStatusCount.isEmpty()) {
                    dashBoardStatusCount = arrayListOf(
                            DashBoardItem(context.getDrawable(R.drawable.ic_dashboard_pending_80dp), context.getString(R.string.pending_leads), "-", LeadStatus.PENDING.value),
                            DashBoardItem(context.getDrawable(R.drawable.ic_dashboard_verified), context.getString(R.string.verified_leads), "-", LeadStatus.VERIFIED.value),
                            DashBoardItem(context.getDrawable(R.drawable.ic_dashboard_declined), context.getString(R.string.declined_leads), "-", LeadStatus.DECLINED.value),
                            DashBoardItem(context.getDrawable(R.drawable.ic_dashboard_disconnected), context.getString(R.string.disconnected_calls), "-", LeadStatus.DISCONNECTED.value),
                            DashBoardItem(context.getDrawable(R.drawable.ic_dashboard_cancelled), context.getString(R.string.cancelled_leads), "-", LeadStatus.CANCELLED.value),
                            DashBoardItem(context.getDrawable(R.drawable.ic_dashboard_expired_80dp), context.getString(R.string.expired_leads), "-", LeadStatus.EXPIRED.value),
                            DashBoardItem(context.getDrawable(R.drawable.ic_self_verified_80dp), context.getString(R.string.self_verified_leads), "-", LeadStatus.SELFVERIFIED.value)
                    )
                }
                if (it?.find { it.status == LeadStatus.SELFVERIFIED.value } != null) {
                    dashBoardStatusCount?.forEach {
                        it?.isSelfVerifiedEnable = true;
                    }
                }
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