package com.tpv.android.network

import DynamicFormReq
import DynamicFormResp
import com.tpv.android.model.network.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {


    //Login
    @POST("login")
    fun logIn(@Body loginReq: LoginReq): Call<CommonResponse<UserDetail>>

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

    //ForgotPassword
    @POST("forgotpassword")
    fun forgotPassword(@Body forgotPasswordReq: ForgotPasswordReq): Call<CommonResponse<Any>>

    //LeadDetail
    @GET("leads/{id}")
    fun getLeadDetail(@Path("id") id: String?): Call<CommonResponse<LinkedHashMap<String?, String?>>>

    //GetForm
    @POST("getform")
    fun getDynamicForm(@Body dynamicFormReq: DynamicFormReq): Call<CommonResponse<DynamicFormResp>>

}
