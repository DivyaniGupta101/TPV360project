package com.tpv.android.ui.home.enrollment

import com.tpv.android.model.ProgramsResp
import com.tpv.android.model.UtilityResp
import com.tpv.android.network.resources.CoroutineScopedViewModel

class SetEnrollViewModel : CoroutineScopedViewModel() {

    var planType: String = ""
    var utilitiesList: ArrayList<UtilityResp?> = ArrayList()
    var programList: ArrayList<ProgramsResp> = ArrayList()


}