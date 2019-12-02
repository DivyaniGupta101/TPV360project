package com.tpv.android.network

import com.tpv.android.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiInterface {


    //    Auth
    @POST("login")
    fun logIn(@Body loginReq: LoginReq): Call<CommonResponse<LoginResp>>


    //    Home
    @POST("logout")
    fun logout(): Call<CommonResponse<Unit>>

    @POST("details")
    fun getProfile(): Call<CommonResponse<UserDetail>>

    @POST("dashboard")
    fun getDashboardDetail(): Call<CommonResponse<List<DashBoard>>>

    @POST("myleads")
    fun getMyLeadList(@Body leadReq: LeadReq): Call<PaginateCommonResp<List<LeadResp>>>


    //    Zipcode
    @POST("zipautocomplete")
    fun zipAutoCompleteApi(@Body zipCodeReq: ZipCodeReq): Call<CommonResponse<List<ZipCodeResp>>>

    @POST("validatezipcode")
    fun getUtility(@Body utilityReq: UtilityReq): Call<CommonResponse<List<UtilityResp>>>

    @POST("getprograms")
    fun getPrograms(@Body programsReq: ProgramsReq): Call<CommonResponse<List<ProgramsResp>>>


    //   SaveLead
    @POST("sendcontract")
    fun sendContract(@Body contractReq: ContractReq): Call<CommonResponse<Any>>

    @Multipart
    @POST("leadmedia")
    fun saveMedia(@Part("leadid") leadid: RequestBody, @Part mediaFile: MultipartBody.Part): Call<CommonResponse<Any>>

    @POST("saveleaddata")
    fun saveLeadDetail(@Body saveLeadsDetailReq: SaveLeadsDetailReq): Call<CommonResponse<SaveLeadsDetailResp>>


    //    Sucess
    @POST("selfverify")
    fun selfVerify(@Body successReq: SuccessReq): Call<CommonResponse<Any>>


    //    OTP
    @POST("generateotp")
    fun sendOtp(@Body otpReq: OTPReq): Call<CommonResponse<Any>>

    @POST("verifyotp")
    fun verifyOtp(@Body verifyOTPReq: VerifyOTPReq): Call<CommonResponse<Any>>

}
