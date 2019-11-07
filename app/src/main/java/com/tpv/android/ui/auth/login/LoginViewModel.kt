package com.tpv.android.ui.auth.login

import com.tpv.android.data.AppRepository
import com.tpv.android.model.LoginReq
import com.tpv.android.network.resources.CoroutineScopedViewModel

class LoginViewModel : CoroutineScopedViewModel() {
    fun logInApi(loginReq: LoginReq) = with(AppRepository) {
        logInCall(loginReq)
    }
}