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


    //Login
    @POST("login")
    fun logIn(@Body loginReq: LoginReq): Call<CommonResponse<LoginResp>>

    //Logout
    @POST("logout")
    fun logout(): Call<CommonResponse<Unit>>

    //Profile
    @POST("details")
    fun getProfile(): Call<CommonResponse<UserDetail>>

    //Dashboard
    @POST("dashboard")
    fun getDashboardDetail(): Call<CommonResponse<List<DashBoard>>>

    //LeadList
    @POST("myleads")
    fun getMyLeadList(@Body leadReq: LeadReq): Call<PaginateCommonResp<List<LeadResp>>>

    //ZipcodeList
    @POST("zipautocomplete")
    fun zipAutoCompleteApi(@Body zipCodeReq: ZipCodeReq): Call<CommonResponse<List<ZipCodeResp>>>

    //UtilityList
    @POST("validatezipcode")
    fun getUtility(@Body utilityReq: UtilityReq): Call<CommonResponse<List<UtilityResp>>>

    //ProgramsList
    @POST("getprograms")
    fun getPrograms(@Body programsReq: ProgramsReq): Call<CommonResponse<List<ProgramsResp>>>

    //SaveContract
    @POST("sendcontract")
    fun sendContract(@Body contractReq: ContractReq): Call<CommonResponse<Any>>

    //Save recording,signature
    @Multipart
    @POST("leadmedia")
    fun saveMedia(@Part("leadid") leadid: RequestBody, @Part mediaFile: MultipartBody.Part): Call<CommonResponse<Any>>

    //Save Lead
    @POST("saveleaddata")
    fun saveLeadDetail(@Body saveLeadsDetailReq: SaveLeadsDetailReq): Call<CommonResponse<SaveLeadsDetailResp>>

    //Verification (email,phone)
    @POST("selfverify")
    fun selfVerify(@Body successReq: SuccessReq): Call<CommonResponse<Any>>

    //Generate OTP
    @POST("generateotp")
    fun sendOtp(@Body otpReq: OTPReq): Call<CommonResponse<Any>>

    //Verify OTP
    @POST("verifyotp")
    fun verifyOtp(@Body verifyOTPReq: VerifyOTPReq): Call<CommonResponse<Any>>

}
