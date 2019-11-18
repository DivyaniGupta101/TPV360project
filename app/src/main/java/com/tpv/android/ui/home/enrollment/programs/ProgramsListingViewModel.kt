package com.tpv.android.ui.home.enrollment.programs

import com.tpv.android.data.AppRepository
import com.tpv.android.model.ProgramsReq
import com.tpv.android.network.resources.CoroutineScopedViewModel

class ProgramsListingViewModel : CoroutineScopedViewModel() {

    fun getPrograms(programsReq: ProgramsReq) = with(AppRepository)
    {
        getProgramsCall(programsReq)
    }
}
