package com.tpv.android.network


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

    //Save recording,signature
    @Multipart
    @POST("leadmedia")
    fun saveMedia(@Part("lng") lng: RequestBody, @Part("lat") lat: RequestBody, @Part("leadid") leadid: RequestBody, @Part mediaFile: MultipartBody.Part): Call<CommonResponse<Any>>

    //Save Lead
    @POST("saveleaddata")
    fun saveLeadDetail(@Body saveLeadsDetailReq: SaveLeadsDetailReq): Call<CommonResponse<SaveLeadsDetailResp>>

    //Save Lead
    @POST("check-lead-validation")
    fun validateLeadDetail(@Body validateLeadsDetailReq: ValidateLeadsDetailReq): Call<CommonResponse<VelidateLeadsDetailResp>>

    //    cancel-lead/21
    @GET("cancel-lead/{id}")
    fun cancelLead(@Path("id") id: String?): Call<CommonResponse<Any>>

    //Verification (email,phone)
    @POST("selfverify")
    fun selfVerify(@Body successReq: SuccessReq): Call<CommonResponse<Any>>

    //Generate OTP For Phone Number
    @POST("generateotp")
    fun sendOtp(@Body otpReq: OTPReq): Call<CommonResponse<Any>>

    //Generate OTP
    @POST("generateotp/email")
    fun sendEmailOtp(@Body otpEmailReq: OTPEmailReq): Call<CommonResponse<Any>>

    //Verify OTP For Phone Number
    @POST("verifyotp")
    fun verifyOtp(@Body verifyOTPReq: VerifyOTPReq): Call<CommonResponse<Any>>

    //Verify OTP For Email Address
    @POST("verifyotp/email")
    fun verifyEmailOtp(@Body verifyOTPEmailReq: VerifyOTPEmailReq): Call<CommonResponse<Any>>

    //ForgotPassword
    @POST("forgotpassword")
    fun forgotPassword(@Body forgotPasswordReq: ForgotPasswordReq): Call<CommonResponse<Any>>

    //LeadDetail
    @GET("leads/{id}")
    fun getLeadDetail(@Path("id") id: String?): Call<CommonResponse<List<DynamicFormResp>>>

    //GetForm
    @POST("getform")
    fun getDynamicForm(@Body dynamicFormReq: DynamicFormReq): Call<CommonResponse<List<DynamicFormResp>>>

    //Get Commodity
    @GET("clients/{id}/forms")
    fun getCommodity(@Path("id") id: String?): Call<CommonResponse<List<CommodityResp>>>

    @POST("https://tpv360.freshdesk.com/api/v2/tickets")
    fun getTickets(@Body ticketReq: TicketReq): Call<Any>

    @POST("agent-activity")
    fun setAgentActivity(@Body agentActivityRequest: AgentActivityRequest): Call<CommonResponse<Any>>

    @POST("agent-locations")
    fun setLocation(@Body agentLocationRequest: AgentLocationRequest): Call<CommonResponse<Any>>

    @GET("agent-current-activity")
    fun getCurrentActivity(): Call<CommonResponse<CurrentActivityResponse>>

    @POST("schedule-tpv-call")
    fun setTPVCall(@Body scheduleTPVCallRequest: ScheduleTPVCallRequest): Call<CommonResponse<Any>>

    //CriticalAlertReport
    @POST("reports/critical-alerts")
    fun getCriticalAlertReportList(@Body clientReportReq: ClientReportReq): Call<PaginateCommonResp<List<ClientReportResp>>>

    //Clients
    @GET("clients")
    fun getClients(): Call<CommonResponse<List<ClientsResp>>>

    //Sales Center
    @GET("sales-centers")
    fun getSalesCenter(@Query("client_id") clientId: String?): Call<CommonResponse<List<ClientsResp>>>

    //TimeLine
    @POST("reports/critical-alerts/{id}/timeline")
    fun getTimeLine(@Path("id") id: String = "0000000635"): Call<CommonResponse<List<ClientTimeLineResp>>>

    //TimeLine
    @POST("reports/critical-alerts/{id}/details")
    fun getClientLeadDetails(@Path("id") id: String ): Call<CommonResponse<ClientLeadDetailResp>>

}
