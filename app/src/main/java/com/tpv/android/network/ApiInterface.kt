package com.tpv.android.network

import com.tpv.android.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {
    @POST("login")
    fun logIn(@Body loginReq: LoginReq): Call<CommonResponse<LogInResp>>

    @POST("logout")
    fun logout(): Call<CommonResponse<Unit>>

    @POST("details")
    fun getProfile(): Call<CommonResponse<UserDetail>>

    @POST("dashboard")
    fun getDashboardDetail(): Call<CommonResponse<List<Dashboard>>>


    @POST("zipautocomplete")
    fun zipAutoCompleteApi(@Field("zipcode") zipcode: String): Call<CommonResponse<List<ZipCodeComplete>>>


    @POST("myleads")
    fun getMyLeadList(@Field("leadstatus") leadstatus: String, @Field("page") page: Int): Call<PaginateCommonResp<List<MyLead>>>


    @POST("getprograms")
    fun getPrograms(@Field("utility_id") utility_id: String, @Field("page") page: Int): Call<PaginateCommonResp<List<Programs>>>


    @POST("sendcontract")
    fun sendContract(@Field("leadid") leadId: String): Call<CommonResponse<Unit>>


    @POST("validatezipcode")
    fun validateZipCode(@Field("zipcode") zipcode: String, @Field("commodity") commodity: String): Call<CommonResponse<List<CommonResponse<List<DuelFuel>>>>>

    @Multipart
    @POST("leadmedia")
    fun leadMedia(@Part("leadid") leadid: RequestBody, @Part mediaFile: MultipartBody.Part): Call<CommonResponse<Unit>>


    @POST("saveleaddata")
    fun saveLeadData(@Field("clientid") clientid: String, @Field("commodity") commodity: String, @Field("utility_id") utility_id: String,
                     @Field("programid") programid: String, @Field("fields") fields: String, @Field("zipcode") zipcode: String
    ): Call<CommonResponse<SaveData>>


    @POST("generateotp")
    fun sendOtp(@Field("phonenumber") phonenumber: String): Call<CommonResponse<Unit>>


    @POST("verifyotp")
    fun verifyOtp(@Field("phonenumber") phonenumber: String, @Field("otp") otp: String): Call<CommonResponse<Unit>>


    @POST("selfverify")
    fun selfVerify(@Field("verification_mode") verification_mode: String, @Field("leadid") leadid: String): Call<CommonResponse<Unit>>

}
