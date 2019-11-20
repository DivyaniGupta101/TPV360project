package com.tpv.android.ui.home.enrollment

import com.tpv.android.model.*
import com.tpv.android.network.resources.CoroutineScopedViewModel

class SetEnrollViewModel : CoroutineScopedViewModel() {

    var planType: String = ""
    var utilitiesList: ArrayList<UtilityResp?> = ArrayList()
    var zipcode: ZipCodeResp? = null
    var programList: ArrayList<ProgramsResp> = ArrayList()
    var serviceDetail: ServiceDetail = ServiceDetail()
    var savedLeadDetail: SaveLeadsDetailResp? = null
}