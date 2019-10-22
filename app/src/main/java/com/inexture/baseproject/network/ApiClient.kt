package com.inexture.baseproject.network

import com.inexture.baseproject.BuildConfig
import com.inexture.baseproject.helper.UserPref
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    lateinit var retrofit: Retrofit
    private val BASE_URL: String = if (BuildConfig.DEBUG) {
        " "
    } else {
        "https://spark.tpv.plus/api/"
    }


    val service: ApiInterface by lazy {
        val builder = Retrofit.Builder()
            .baseUrl(BASE_URL)
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
                    "Bearer ${UserPref.token}"
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

        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT)
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            client.addInterceptor(loggingInterceptor)
        }

        builder.client(client.build())
        retrofit = builder.build()
        retrofit.create(ApiInterface::class.java)
    }
}