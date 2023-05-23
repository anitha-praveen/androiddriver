package com.rodaClone.driver.connection

import com.rodaClone.driver.ut.Config
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import retrofit2.http.DELETE


/**
 * Interface used to do all the apis
 * using the endpoints from base url
 * */
interface ConnectionHelper {

    @FormUrlEncoded
    @POST(Config.LANGUAGE_CODE_API)
    fun getAvailableLanguageApi(
        @HeaderMap headerMap: Map<String, String>,
        @FieldMap hashMap: HashMap<String, String>
    ): Call<BaseResponse>


    @GET(Config.LANGUAGE_CODE_API + "/{" + Config.Code + "}")
    fun getSelectedLangApi(
        @Path(Config.Code) requestId: String?
    ): Call<BaseResponse>

    @FormUrlEncoded
    @POST(Config.SIGN_IN_API)
    fun signIn(
        @HeaderMap headerMap: Map<String, String>,
        @FieldMap hashMap: HashMap<String, String>
    ): Call<BaseResponse>

    @GET(Config.TYPES_LIST_API)
    fun typeApi(
        @HeaderMap headerMap: Map<String, String>
    ): Call<BaseResponse>

    @GET(Config.SERVICE_LOCATION_LIST)
    fun getServiceLocationApi(
        @HeaderMap headerMap: Map<String, String>
    ): Call<BaseResponse>


    @FormUrlEncoded
    @POST(Config.SIGN_UP_API)
    fun signUpApi(
        @HeaderMap headerMap: Map<String, String>,
        @FieldMap hashMap: HashMap<String, String>
    ): Call<BaseResponse>


    @FormUrlEncoded
    @POST(Config.AUTH_TOKEN_API)
    fun authTokenApi(
        @HeaderMap headerMap: Map<String, String>,
        @FieldMap hashMap: HashMap<String, String>
    ): Call<BaseResponse>


    @GET(Config.DRIVER_REQ_PROGRESS)
    fun driverReqProgressApi(
        @HeaderMap headerMap: Map<String, String>,
    ): Call<BaseResponse>

    @GET(Config.DRIVER_PROFILE)
    fun driverGetProfileApi(
        @HeaderMap headerMap: Map<String, String>,
    ): Call<BaseResponse>

    @Multipart
    @POST(Config.DRIVER_PROFILE)
    fun updateProfileImageApi(
        @HeaderMap headerMap: Map<String, String>,
        @Part file: MultipartBody.Part
    ): Call<BaseResponse>

    @Multipart
    @POST(Config.DRIVER_PROFILE)
    fun updateProfileApi(
        @HeaderMap headerMap: Map<String, String>,
        @PartMap() partMap: Map<String, @JvmSuppressWildcards RequestBody>
    ): Call<BaseResponse>

    @FormUrlEncoded
    @POST(Config.CHECK_PHONE_NUMBER_AVAILABLE)
    fun checkPhoneNumberApi(
        @HeaderMap headerMap: Map<String, String>,
        @FieldMap hashMap: HashMap<String, String>
    ): Call<BaseResponse>

    @GET(Config.GET_DOC_LIST)
    fun getDocListApi(@HeaderMap headerMap: Map<String, String>): Call<BaseResponse>

    @GET(Config.GROUP_DOCUMENT)
    fun getGroupDocument(@HeaderMap headerMap: Map<String, String>): Call<BaseResponse>

    @Multipart
    @POST(Config.UPLOAD_DOCUMENT)
    fun uploadDocumentApi(
        @HeaderMap headerMap: Map<String, String>,
        @PartMap partMap: Map<String, @JvmSuppressWildcards RequestBody>?,
        @Part image: MultipartBody.Part
    ): Call<BaseResponse>


    @GET(Config.GET_COMPLAINTS_LIST)
    fun getComplaintsListApi(@HeaderMap headerMap: Map<String, String>): Call<BaseResponse>

    @GET(Config.DISPUTE)
    fun getTripComplaintsListApi(@HeaderMap headerMap: Map<String, String>): Call<BaseResponse>

    @FormUrlEncoded
    @POST(Config.POST_COMPLAINT)
    fun postComplaintApi(
        @HeaderMap headerMap: Map<String, String>,
        @FieldMap hashMap: HashMap<String, String>
    ): Call<BaseResponse>

    @GET(Config.GET_FAQ)
    fun getFAQListApi(@HeaderMap headerMap: Map<String, String>): Call<BaseResponse>

    @GET(Config.GET_SOS)
    fun getSOSListApi(@HeaderMap headerMap: Map<String, String>): Call<BaseResponse>

    @GET(Config.ONLINE_OFFLINE)
    fun driverStatus(@HeaderMap headerMap: Map<String, String>): Call<BaseResponse>

    @FormUrlEncoded
    @POST(Config.REQUEST_RESPOND)
    fun requestRespondApi(
        @HeaderMap headerMap: Map<String, String>,
        @FieldMap hashMap: HashMap<String, String>
    ): Call<BaseResponse>

    @Multipart
    @POST(Config.TRIP_START)
    fun requestStatWithImage(
        @HeaderMap headerMap: Map<String, String>,
        @PartMap() partMap: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part file: MultipartBody.Part
    ): Call<BaseResponse>

    @Multipart
    @POST(Config.TRIP_START)
    fun requestStart(
        @HeaderMap headerMap: Map<String, String>,
        @PartMap() partMap: Map<String, @JvmSuppressWildcards RequestBody>
    ): Call<BaseResponse>


    @FormUrlEncoded
    @POST(Config.TRIP_ARRIVED)
    fun requestArrived(
        @HeaderMap headerMap: Map<String, String>,
        @FieldMap hashMap: HashMap<String, String>
    ): Call<BaseResponse>


    @Multipart
    @POST(Config.TRIP_END)
    fun requestEnd(
        @HeaderMap headerMap: Map<String, String>,
        @PartMap() partMap: Map<String, @JvmSuppressWildcards RequestBody>
    ): Call<BaseResponse>

    @Multipart
    @POST(Config.TRIP_END)
    fun requestEndWithImage(
        @HeaderMap headerMap: Map<String, String>,
        @PartMap() partMap: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part file: MultipartBody.Part
    ): Call<BaseResponse>

    @POST(Config.CANCELATION_LIST)
    fun requestCancelList(
        @HeaderMap headerMap: Map<String, String>
    ): Call<BaseResponse>

    @FormUrlEncoded
    @POST(Config.UPDATE_CANCEL)
    fun updateCancel(
        @HeaderMap headerMap: Map<String, String>,
        @FieldMap hashMap: HashMap<String, String>
    ): Call<BaseResponse>

    @FormUrlEncoded
    @POST(Config.HISTORY_URL)
    fun getHistoryListApi(
        @HeaderMap headerMap: Map<String, String>,
        @FieldMap hashMap: HashMap<String, String>,
        @Query("page") pageNo: String
    ): Call<BaseResponse>

    @FormUrlEncoded
    @POST(Config.RATE)
    fun rateUserApi(
        @HeaderMap headerMap: Map<String, String>,
        @FieldMap hashMap: HashMap<String, String>
    ): Call<BaseResponse>

    @FormUrlEncoded
    @POST(Config.SAVE_SOS)
    fun saveSosApi(
        @HeaderMap headerMap: Map<String, String>,
        @FieldMap hashMap: HashMap<String, String>
    ): Call<BaseResponse>

    @DELETE(Config.DELETE_SOS)
    fun deleteSosApi(
        @HeaderMap headerMap: Map<String, String>,
        @Path("id") inputId: String
    ): Call<BaseResponse>

    @GET(Config.LOG_OUT_URL)
    fun getLogOutApi(@HeaderMap headerMap: Map<String, String>): Call<BaseResponse>

    @GET(Config.WALLET_LIST_URL)
    fun getWalletListApi(@HeaderMap headerMap: Map<String, String>): Call<BaseResponse>

    @FormUrlEncoded
    @POST(Config.WALLET_ADD_AMOUNT)
    fun getWalletAddAmount(
        @HeaderMap headerMap: Map<String, String>,
        @FieldMap hashMap: HashMap<String, String>,
    ): Call<BaseResponse>

    @GET(Config.GET_DASH_DETAILS)
    fun getDashDetailsApi(@HeaderMap headerMap: Map<String, String>): Call<BaseResponse>

    @GET(Config.GET_SUBSCRIPTION_LIST)
    fun getSubscriptionList(@HeaderMap headerMap: Map<String, String>): Call<BaseResponse>


    @GET(Config.NOTIFIACTION_URL)
    fun getNotificationApi(
        @HeaderMap headerMap: Map<String, String>,
        @Query("page") pageNo: String
    ): Call<BaseResponse>

    @FormUrlEncoded
    @POST(Config.SUBSCRIPTION_ADD)
    fun subscriptionAdd(
        @HeaderMap headerMap: Map<String, String>,
        @FieldMap hashMap: HashMap<String, String>,
    ): Call<BaseResponse>

    @FormUrlEncoded
    @POST(Config.LOCATION_APPROVE)
    fun locationApprove(
        @HeaderMap headerMap: Map<String, String>,
        @FieldMap hashMap: HashMap<String, String>,
    ): Call<BaseResponse>

    @FormUrlEncoded
    @POST(Config.CREATE_REQUEST)
    fun createRequestApi(
        @HeaderMap headerMap: Map<String, String>,
        @FieldMap hashMap: HashMap<String, String>
    ): Call<BaseResponse>

    @GET(Config.GET_COMPANY_LIST)
    fun getCompanyListsApi(@HeaderMap headerMap: Map<String, String>): Call<BaseResponse>

    @FormUrlEncoded
    @POST(Config.GET_VEHICLE_MODELS)
    fun getVehicleModelsApi(
        @HeaderMap headerMap: Map<String, String>,
        @FieldMap hashMap: HashMap<String, String>
    ): Call<BaseResponse>

    @FormUrlEncoded
    @POST(Config.CUSTOM_OTP_URL)
    fun sendCustomOtp(
        @HeaderMap headerMap: Map<String, String>,
        @FieldMap hashMap: HashMap<String, String>
    ): Call<BaseResponse>

    @FormUrlEncoded
    @POST(Config.SELECTED_LANGUAGE)
    fun getPostSelectedLanguageBase(
        @HeaderMap headerMap: Map<String, String>,
        @FieldMap hashMap: HashMap<String, String>
    ): Call<BaseResponse>

    @GET(Config.GET_REFERAL_API)
    fun getDriverReferalApi(@HeaderMap headerMap: Map<String, String>): Call<BaseResponse>

    @FormUrlEncoded
    @POST(Config.SINGNLE_HISTORY)
    fun getSingleHistory(
        @HeaderMap headerMap: Map<String, String>,
        @FieldMap hashMap: HashMap<String, String>
    ): Call<BaseResponse>

    @Multipart
    @POST(Config.NIGHT_IMAGE_UPLOAD)
    fun updateNightImage(
        @HeaderMap headerMap: Map<String, String>,
        @PartMap() partMap: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part file: MultipartBody.Part
    ): Call<BaseResponse>

    @FormUrlEncoded
    @POST(Config.SKIP_NIGHT_IMG)
    fun skipNightImgApi(
        @HeaderMap headerMap: Map<String, String>,
        @FieldMap hashMap: HashMap<String, String>
    ): Call<BaseResponse>

    @FormUrlEncoded
    @POST(Config.RETAKE_NIGHT)
    fun requestRetakeApi(
        @HeaderMap headerMap: Map<String, String>,
        @FieldMap hashMap: HashMap<String, String>
    ): Call<BaseResponse>

    @Multipart
    @POST(Config.NIGHT_IMAGE_BOTH)
    fun uploadNightBoth(
        @HeaderMap headerMap: Map<String, String>,
        @PartMap() partMap: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part driver: MultipartBody.Part,
        @Part user: MultipartBody.Part
    ): Call<BaseResponse>

    @FormUrlEncoded
    @POST(Config.ORDERID_API)
    fun getOrderApi(
        @HeaderMap headerMap: Map<String, String>,
        @FieldMap hashMap: HashMap<String, String>,
    ): Call<BaseResponse>

}