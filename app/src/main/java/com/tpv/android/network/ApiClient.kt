package com.tpv.android.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    lateinit var retrofit: Retrofit
    private val BASE_URL: String = if (com.tpv.android.BuildConfig.DEBUG) {
        " "
    } else {
        "https://spark.tpv.plus/api/"
    }


    val service: com.tpv.android.network.ApiInterface by lazy {
        val builder = Retrofit.Builder()
            .baseUrl(com.tpv.android.network.ApiClient.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
        val client = OkHttpClient.Builder()
        client.connectTimeout(30, TimeUnit.SECONDS)
        client.readTimeout(30, TimeUnit.SECONDS)
        client.writeTimeout(30, TimeUnit.SECONDS)

        val interceptor = Interceptor {
            var request = it.request()

            try {
                val newBuilder = request.newBuilder()
                newBuilder.addHeader(
                    "Authorization",
                    "Bearer ${com.tpv.android.helper.UserPref.token}"
                )
                newBuilder.addHeader(
                        "Accept",
                        "application/json"
                )
                request = newBuilder.build()
            } catch (e: Exception) {
            }

            val response = it.proceed(request)
            response
        }
        client.addInterceptor(interceptor)

        if (com.tpv.android.BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT)
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            client.addInterceptor(loggingInterceptor)
        }

        builder.client(client.build())
        com.tpv.android.network.ApiClient.retrofit = builder.build()
        com.tpv.android.network.ApiClient.retrofit.create(com.tpv.android.network.ApiInterface::class.java)
    }
}