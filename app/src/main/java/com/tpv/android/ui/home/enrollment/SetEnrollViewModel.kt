package com.tpv.android.ui.home.enrollment

import com.tpv.android.model.SelectedUtilityResp
import com.tpv.android.network.resources.CoroutineScopedViewModel

class SetEnrollViewModel : CoroutineScopedViewModel() {

    var selectedUtility: String = ""
    var selectedUtilities: ArrayList<SelectedUtilityResp> = ArrayList()


}