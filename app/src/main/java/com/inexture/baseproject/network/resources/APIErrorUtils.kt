package com.inexture.baseproject.network.resources

import com.inexture.baseproject.network.ApiClient
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Response


object APIErrorUtils {

    /**
     * parse API error form errorBody()
     */
    inline fun <T, reified F> parseError(response: Response<T>): F? {
        val converter: Converter<ResponseBody, F> = ApiClient.retrofit
            .responseBodyConverter(F::class.java, arrayOfNulls<Annotation>(0))
        return try {
            response.errorBody()?.let {
                converter.convert(it)
            }
        } catch (e: Exception) {
            return null
        }

    }
}