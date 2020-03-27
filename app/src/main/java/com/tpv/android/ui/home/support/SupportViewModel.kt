package com.tpv.android.ui.home.support

import com.tpv.android.data.AppRepository
import com.tpv.android.model.network.TicketReq
import com.tpv.android.network.resources.CoroutineScopedViewModel

class SupportViewModel : CoroutineScopedViewModel() {

    fun getTicket(ticketReq: TicketReq) = with(AppRepository) {
        getTicketCall(ticketReq)
    }

}