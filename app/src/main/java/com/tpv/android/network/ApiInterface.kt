package com.tpv.android.network

import com.tpv.android.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Field

interface ApiInterface {
    @POST("login")
    fun logIn(@Body loginReq: LoginReq): Call<CommonResponse<LoginResp>>

    @POST("logout")
    fun logout(): Call<CommonResponse<Unit>>

    @POST("details")
    fun getProfile(): Call<CommonResponse<UserDetail>>

    @POST("dashboard")
    fun getDashboardDetail(): Call<CommonResponse<List<Dashboard>>>


    @POST("zipautocomplete")
    fun zipAutoCompleteApi(@Body zipCodeReq: ZipCodeReq): Call<CommonResponse<List<ZipCodeResp>>>


    @POST("getprograms")
    fun getPrograms(@Body programsReq: ProgramsReq): Call<CommonResponse<List<ProgramsResp>>>


    @POST("myleads")
    fun getMyLeadList(@Body leadReq: LeadReq): Call<PaginateCommonResp<List<LeadResp>>>


    @POST("sendcontract")
    fun sendContract(@Field("leadid") leadId: String): Call<CommonResponse<Unit>>


    @POST("validatezipcode")
    fun getUtility(@Body utilityReq: UtilityReq): Call<CommonResponse<List<UtilityResp>>>

    @Multipart
    @POST("leadmedia")
    fun saveRecording(@Part("leadid") leadid: RequestBody, @Part mediaFile: MultipartBody.Part): Call<CommonResponse<RecordingResp>>


    @POST("saveleaddata")
    fun saveLeadDetail(@Body saveLeadsDetailReq: SaveLeadsDetailReq): Call<CommonResponse<SaveLeadsDetailResp>>


    @POST("generateotp")
    fun sendOtp(@Field("phonenumber") phonenumber: String): Call<CommonResponse<Unit>>


    @POST("verifyotp")
    fun verifyOtp(@Field("phonenumber") phonenumber: String, @Field("otp") otp: String): Call<CommonResponse<Unit>>


    @POST("selfverify")
    fun selfVerify(@Field("verification_mode") verification_mode: String, @Field("leadid") leadid: String): Call<CommonResponse<Unit>>

}
