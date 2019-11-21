package com.tpv.android.ui.home.enrollment.recording

import com.tpv.android.data.AppRepository
import com.tpv.android.network.resources.CoroutineScopedViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody

class RecordingViewModel : CoroutineScopedViewModel() {

    fun saveRecording(leadId: RequestBody, mediaFile: MultipartBody.Part) = with(AppRepository) {
        saveRecordingCall(leadId, mediaFile)
    }

}