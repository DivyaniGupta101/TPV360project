package com.inexture.baseproject.network

import com.inexture.baseproject.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {
    @GET("search/repositories")
    fun searchRepos(@Query("q") query: String, @Query("page") page: Int, @Query("per_page") perPage: Int = 2): Call<RepoSearchResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(@Field("email") email: String, @Field("password") password: String): Call<CommonResponse<LogInResp>>

    @POST("logout")
    fun logout(): Call<CommonResponse<Unit>>

    @POST("details")
    fun getUserDetail(): Call<CommonResponse<UserDetail>>

    @POST("dashboard")
    fun getDashboardDetail(): Call<CommonResponse<List<Dashboard>>>

    @FormUrlEncoded
    @POST("zipautocomplete")
    fun zipAutoCompleteApi(@Field("zipcode") zipcode: String): Call<CommonResponse<List<ZipCodeComplete>>>

    @FormUrlEncoded
    @POST("myleads")
    fun getMyLeadList(@Field("leadstatus") leadstatus: String, @Field("page") page: Int): Call<PaginateCommonResp<List<MyLead>>>

    @FormUrlEncoded
    @POST("getprograms")
    fun getPrograms(@Field("utility_id") utility_id: String, @Field("page") page: Int): Call<PaginateCommonResp<List<Programs>>>

    @FormUrlEncoded
    @POST("sendcontract")
    fun sendContract(@Field("leadid") leadId: String): Call<CommonResponse<Unit>>

    @FormUrlEncoded
    @POST("validatezipcode")
    fun validateZipCode(@Field("zipcode") zipcode: String, @Field("commodity") commodity: String): Call<CommonResponse<List<ValidateZipCode>>>

    @FormUrlEncoded
    @POST("getform")
    fun getForm(@Field("clientid") clientid: String, @Field("commodity") commodity: String, @Field("programid") programid: String): Call<CommonResponse<List<GetForm>>>

    @FormUrlEncoded
    @POST("getform")
    fun getFormDuelForm(@Field("clientid") clientid: String, @Field("commodity") commodity: String, @Field("programid") programid: String): Call<CommonResponse<List<DuelFuel>>>

    @Multipart
    @POST("leadmedia")
    fun leadMedia(@Part("leadid") leadid: RequestBody, @Part mediaFile: MultipartBody.Part): Call<CommonResponse<Unit>>

    @FormUrlEncoded
    @POST("saveleaddata")
    fun saveLeadData(@Field("clientid") clientid: String, @Field("commodity") commodity: String, @Field("utility_id") utility_id: String,
                     @Field("programid") programid: String, @Field("fields") fields: String, @Field("zipcode") zipcode: String
    ): Call<CommonResponse<SaveData>>

    @FormUrlEncoded
    @POST("saveleaddata")
    fun saveDuelData(@Field("clientid") clientid: String, @Field("commodity") commodity: String,
                     @Field("gasutility_id") gasutility_id: String,
                     @Field("gasprogramid") gasprogramid: String, @Field("electricutility_id") electricutility_id: String,
                     @Field("electricprogramid") electricprogramid: String, @Field("fields") fields: String, @Field("zipcode") zipcode: String
    ): Call<CommonResponse<SaveData>>

    @FormUrlEncoded
    @POST("generateotp")
    fun sendOtp(@Field("phonenumber") phonenumber: String): Call<CommonResponse<Unit>>

    @FormUrlEncoded
    @POST("verifyotp")
    fun verifyOtp(@Field("phonenumber") phonenumber: String, @Field("otp") otp: String): Call<CommonResponse<Unit>>

    @FormUrlEncoded
    @POST("selfverify")
    fun selfVerify(@Field("verification_mode") verification_mode: String, @Field("leadid") leadid: String): Call<CommonResponse<Unit>>

}
