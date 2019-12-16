package com.tpv.android.ui.home.enrollment.dynamicform


import com.tpv.android.data.AppRepository
import com.tpv.android.model.network.DynamicFormReq
import com.tpv.android.network.resources.CoroutineScopedViewModel

class DynamicFormViewModel : CoroutineScopedViewModel() {
    fun getDynamicForm(dynamicFormReq: DynamicFormReq) = with(AppRepository) {
        getDynamicFormCall(dynamicFormReq)
    }
}