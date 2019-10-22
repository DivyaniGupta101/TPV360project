package com.inexture.baseproject.network.resources

import android.util.Log
import com.livinglifetechway.k4kotlin.core.TAG
import com.livinglifetechway.k4kotlin_retrofit.enqueueDeferredResponse
import retrofit2.Call


/**
 * This extension method enqueues the call using the coroutine and
 * return the Result instance with Success or Failure
 */
suspend fun <T : Any> Call<T>.getResult(): Result<T, APIError> {
    return try {
        val response = this.enqueueDeferredResponse().await()
        if (response.isSuccessful) {
            Success(response.body())
        } else {
            //parse error from API
            val apiError = APIErrorUtils.parseError<T, APIError>(response)
            Failure(null, apiError)
        }
    } catch (throwable: Throwable) {
        Log.e(TAG, "getResult: ", throwable)
        Failure(throwable, null)
    }
}






