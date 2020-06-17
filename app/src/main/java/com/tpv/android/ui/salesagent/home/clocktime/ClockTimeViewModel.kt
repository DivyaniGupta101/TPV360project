package com.tpv.android.ui.salesagent.home.clocktime

import com.tpv.android.data.AppRepository
import com.tpv.android.model.network.AgentActivityRequest
import com.tpv.android.model.network.AgentLocationRequest
import com.tpv.android.network.resources.CoroutineScopedViewModel

class ClockTimeViewModel : CoroutineScopedViewModel() {
    fun getCurrentActivity() = with(AppRepository) {
        getCurrentActivityCall()
    }

    fun setAgentActivity(agentActivityRequest: AgentActivityRequest) = with(AppRepository)
    {
        setAgentActivityCall(agentActivityRequest)
    }

    fun setLocation(agentLocationRequest: AgentLocationRequest) = with(AppRepository)
    {
        setLocationCall(agentLocationRequest)
    }
}