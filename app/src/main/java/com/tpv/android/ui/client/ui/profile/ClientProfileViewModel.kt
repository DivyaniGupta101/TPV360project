package com.tpv.android.ui.client.ui.profile

import com.tpv.android.data.AppRepository
import com.tpv.android.network.resources.CoroutineScopedViewModel
import okhttp3.MultipartBody

class ClientProfileViewModel : CoroutineScopedViewModel() {
    fun getProfile() = with(AppRepository) {
        getProfileDetailCall()
    }

    fun updateProfilePhoto(mediaFile: MultipartBody.Part) = with(AppRepository) {
        updateProfilePhotoCall(file = mediaFile)
    }
}