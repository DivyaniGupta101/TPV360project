package com.tpv.android.ui.home.enrollment

import com.tpv.android.model.UtilityResp
import com.tpv.android.network.resources.CoroutineScopedViewModel

class SetEnrollViewModel : CoroutineScopedViewModel() {

    var selectedUtility: String = ""
    var utilities: ArrayList<UtilityResp?> = ArrayList()

}