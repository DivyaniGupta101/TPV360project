package com.tpv.android.ui.home.enrollment.dynamicform

import DynamicFormReq
import com.tpv.android.data.AppRepository
import com.tpv.android.network.resources.CoroutineScopedViewModel

class DynamicFormViewModel : CoroutineScopedViewModel() {
    fun getDynamicForm(dynamicFormReq: DynamicFormReq) = with(AppRepository) {
        getDynamicFormCall(dynamicFormReq)
    }
}