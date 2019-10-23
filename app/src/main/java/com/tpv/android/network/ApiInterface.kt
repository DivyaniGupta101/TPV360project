package com.tpv.android.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {
    @FormUrlEncoded
    @POST("login")
    fun login(@Field("email") email: String, @Field("password") password: String): Call<com.tpv.android.model.CommonResponse<com.tpv.android.model.LogInResp>>

    @POST("logout")
    fun logout(): Call<com.tpv.android.model.CommonResponse<Unit>>

    @POST("details")
    fun getUserDetail(): Call<com.tpv.android.model.CommonResponse<com.tpv.android.model.UserDetail>>

    @POST("dashboard")
    fun getDashboardDetail(): Call<com.tpv.android.model.CommonResponse<List<com.tpv.android.model.Dashboard>>>

    @FormUrlEncoded
    @POST("zipautocomplete")
    fun zipAutoCompleteApi(@Field("zipcode") zipcode: String): Call<com.tpv.android.model.CommonResponse<List<com.tpv.android.model.ZipCodeComplete>>>

    @FormUrlEncoded
    @POST("myleads")
    fun getMyLeadList(@Field("leadstatus") leadstatus: String, @Field("page") page: Int): Call<com.tpv.android.model.PaginateCommonResp<List<com.tpv.android.model.MyLead>>>

    @FormUrlEncoded
    @POST("getprograms")
    fun getPrograms(@Field("utility_id") utility_id: String, @Field("page") page: Int): Call<com.tpv.android.model.PaginateCommonResp<List<com.tpv.android.model.Programs>>>

    @FormUrlEncoded
    @POST("sendcontract")
    fun sendContract(@Field("leadid") leadId: String): Call<com.tpv.android.model.CommonResponse<Unit>>

    @FormUrlEncoded
    @POST("validatezipcode")
    fun validateZipCode(@Field("zipcode") zipcode: String, @Field("commodity") commodity: String): Call<com.tpv.android.model.CommonResponse<List<com.tpv.android.model.ValidateZipCode>>>

    @FormUrlEncoded
    @POST("getform")
    fun getForm(@Field("clientid") clientid: String, @Field("commodity") commodity: String, @Field("programid") programid: String): Call<com.tpv.android.model.CommonResponse<List<com.tpv.android.model.GetForm>>>

    @FormUrlEncoded
    @POST("getform")
    fun getFormDuelForm(@Field("clientid") clientid: String, @Field("commodity") commodity: String, @Field("programid") programid: String): Call<com.tpv.android.model.CommonResponse<List<com.tpv.android.model.DuelFuel>>>

    @Multipart
    @POST("leadmedia")
    fun leadMedia(@Part("leadid") leadid: RequestBody, @Part mediaFile: MultipartBody.Part): Call<com.tpv.android.model.CommonResponse<Unit>>

    @FormUrlEncoded
    @POST("saveleaddata")
    fun saveLeadData(@Field("clientid") clientid: String, @Field("commodity") commodity: String, @Field("utility_id") utility_id: String,
                     @Field("programid") programid: String, @Field("fields") fields: String, @Field("zipcode") zipcode: String
    ): Call<com.tpv.android.model.CommonResponse<com.tpv.android.model.SaveData>>

    @FormUrlEncoded
    @POST("saveleaddata")
    fun saveDuelData(@Field("clientid") clientid: String, @Field("commodity") commodity: String,
                     @Field("gasutility_id") gasutility_id: String,
                     @Field("gasprogramid") gasprogramid: String, @Field("electricutility_id") electricutility_id: String,
                     @Field("electricprogramid") electricprogramid: String, @Field("fields") fields: String, @Field("zipcode") zipcode: String
    ): Call<com.tpv.android.model.CommonResponse<com.tpv.android.model.SaveData>>

    @FormUrlEncoded
    @POST("generateotp")
    fun sendOtp(@Field("phonenumber") phonenumber: String): Call<com.tpv.android.model.CommonResponse<Unit>>

    @FormUrlEncoded
    @POST("verifyotp")
    fun verifyOtp(@Field("phonenumber") phonenumber: String, @Field("otp") otp: String): Call<com.tpv.android.model.CommonResponse<Unit>>

    @FormUrlEncoded
    @POST("selfverify")
    fun selfVerify(@Field("verification_mode") verification_mode: String, @Field("leadid") leadid: String): Call<com.tpv.android.model.CommonResponse<Unit>>

}
