package com.tpv.android.network

import android.util.Base64
import com.google.gson.GsonBuilder
import com.tpv.android.BuildConfig
import com.tpv.android.helper.Pref
import com.tpv.android.utils.AppConfig
import com.tpv.android.utils.AppConstant
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    lateinit var retrofit: Retrofit
    private val BASE_URL: String = AppConfig.BASEURL
    // Server URLS :
    // "https://dev.tpv.plus/api/"
    // "http://192.168.0.19:8000/api/"
    // "https://newdev.tpv.plus/api/"
    // "https://newdemo.tpv.plus/api/"
    // "https://test.tpv.plus/api/"
    // "https://spark.tpv.plus/api/"

    val service: ApiInterface by lazy {
        val builder = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().serializeNulls().create()))
        val client = OkHttpClient.Builder()
        client.connectTimeout(30, TimeUnit.SECONDS)
        client.readTimeout(30, TimeUnit.SECONDS)
        client.writeTimeout(30, TimeUnit.SECONDS)

        val interceptor = Interceptor {
            var request = it.request()

            try {
                val newBuilder = request.newBuilder()

                if (!Pref.token.isNullOrEmpty() && request.url().toString().startsWith(BASE_URL)) {
                    newBuilder.addHeader(
                            "Authorization",
                            "Bearer ${Pref.token}"
                    )
                    newBuilder.addHeader(
                            "Content-type",
                            "application/json"
                    )
                    request = newBuilder.build()
                }
                if (!Pref.token.isNullOrEmpty() && !request.url().toString().startsWith(BASE_URL)) {
                    newBuilder.addHeader(
                            "Authorization",
                            getAuthToken()
                    )
                    newBuilder.addHeader(
                            "Content-type",
                            "application/json"
                    )
                    request = newBuilder.build()
                }
            } catch (e: Exception) {
            }

            val response = it.proceed(request)
            if (response.code() == 401) {
                UnAuthorizedEventObserver.notifyObservers()
                return@Interceptor Response.Builder()
                        .code(200) //Whatever code
                        .protocol(Protocol.HTTP_2)
                        .message("")
                        .body(ResponseBody.create(MediaType.parse("application/json"), "{}"))
                        .request(it.request())
                        .build()
            }
            response
        }
        client.addInterceptor(interceptor)

        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT)
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            client.addInterceptor(loggingInterceptor)
        }

        builder.client(client.build())
        retrofit = builder.build()
        retrofit.create(ApiInterface::class.java)
    }

    private fun getAuthToken(): String {
        val authToken = AppConstant.TICKET_USERNAME
        return "Basic " + Base64.encodeToString(
                authToken.toByteArray(Charsets.UTF_8),
                Base64.NO_WRAP
        )
    }
}