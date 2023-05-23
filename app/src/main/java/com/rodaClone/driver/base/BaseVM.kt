package com.rodaClone.driver.base

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.gson.Gson
import com.rodaClone.driver.R
import com.rodaClone.driver.connection.*
import com.rodaClone.driver.ut.SessionMaintainence
import com.rodaClone.driver.ut.Utilz.convertToException
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * This activity to be extended to All ViewModel
 * it acts as a Repository in MVVM Model
 * */
abstract class BaseVM<T : BaseResponse, N : BaseViewOperator> constructor(
    private val session: SessionMaintainence,
    private val mConnection: ConnectionHelper
) : ViewModel(), Basecallback<T> {
    var translationModel: TranslationModel? = null
    var isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    private var map: HashMap<String, String> = HashMap()
    private lateinit var mNavigator: N
    var requestbody = HashMap<String, RequestBody>()
    var body: MultipartBody.Part? = null

    init {
        map["Accept"] = "application/json"

        if (session.getString(SessionMaintainence.AccessToken) != null)
            map["Authorization"] = "Bearer " + session.getString(SessionMaintainence.AccessToken)

        if (session.getString(SessionMaintainence.CURRENT_LANGUAGE) != null)
            map["Content-Language"] = session.getString(SessionMaintainence.CURRENT_LANGUAGE) + ""

        if (!session.getString(SessionMaintainence.TRANSLATED_DATA).isNullOrEmpty())
            translationModel = Gson().fromJson(
                session.getString(SessionMaintainence.TRANSLATED_DATA),
                TranslationModel::class.java
            )
    }

    fun printE(tag: String, value: String) {
        Log.e(tag, value)
    }

    fun printD(tag: String, value: String) {
//        Log.d(tag, value)
    }

    fun printV(tag: String, value: String) {
//        Log.v(tag, value)
    }

    fun printI(tag: String, value: String) {
//        Log.i(tag, value)
    }

    fun getLanguageBase(hashMap: HashMap<String, String>) {
        mConnection.getAvailableLanguageApi(map, hashMap)
            .enqueue(baseCalback as Callback<BaseResponse>)
    }

    fun getSelectedLangBase(code: String) {
        isLoading.postValue(true)
        mConnection.getSelectedLangApi(code)!!.enqueue(baseCalback as Callback<BaseResponse>)
    }

    fun loginOrSignUpBase(hashMap: HashMap<String, String>) {
        mConnection.signIn(map, hashMap)
            .enqueue(baseCalback as Callback<BaseResponse>)
    }

    fun typesBase() {
        mConnection.typeApi(map)
            .enqueue(baseCalback as Callback<BaseResponse>)
    }

    fun serviceLocBase() {
        mConnection.getServiceLocationApi(map)
            .enqueue(baseCalback as Callback<BaseResponse>)
    }

    fun signUpBase(hashMap: HashMap<String, String>) {
        isLoading.postValue(true)
        mConnection.signUpApi(map, hashMap)
            .enqueue(baseCalback as Callback<BaseResponse>)
    }

    fun authTokenCallBase(hashMap: HashMap<String, String>) {
        mConnection.authTokenApi(map, hashMap)
            .enqueue(baseCalback as Callback<BaseResponse>)
    }

    fun driverReqProgressBase() {
        mConnection.driverReqProgressApi(map)
            .enqueue(baseCalback as Callback<BaseResponse>)
    }

    fun driverGetProfileBase() {
        isLoading.postValue(true)
        mConnection.driverGetProfileApi(map)
            .enqueue(baseCalback as Callback<BaseResponse>)
    }

    fun updateProfileImageApiBase() {
        isLoading.postValue(true)
        mConnection.updateProfileImageApi(map, body!!)
            .enqueue(baseCalback as Callback<BaseResponse>)
    }

    fun updateProfileBase() {
        isLoading.postValue(true)
        mConnection.updateProfileApi(map, requestbody)
            .enqueue(baseCalback as Callback<BaseResponse>)
    }

    fun checkPhoneNumberAvailabilityBase(hashMap: HashMap<String, String>) {
        isLoading.postValue(true)
        mConnection.checkPhoneNumberApi(map, hashMap).enqueue(baseCalback as Callback<BaseResponse>)
    }

    fun getDocumentListBase() {
        isLoading.postValue(true)
        mConnection.getDocListApi(map).enqueue(baseCalback as Callback<BaseResponse>)
    }

    fun getGroupDocumentBase(){
        isLoading.postValue(true)
        mConnection.getGroupDocument(map).enqueue(baseCalback as Callback<BaseResponse>)
    }

    fun uploadDocumentBase() {
        isLoading.postValue(true)
        mConnection.uploadDocumentApi(
            map,
            requestbody,
            body!!
        ).enqueue(baseCalback as Callback<BaseResponse>)
    }



    fun getComplaintsListBase() {
        isLoading.postValue(true)
        mConnection.getComplaintsListApi(map).enqueue(baseCalback as Callback<BaseResponse>)
    }

    fun getTripComplaintsListBase() {
        isLoading.postValue(true)
        mConnection.getTripComplaintsListApi(map).enqueue(baseCalback as Callback<BaseResponse>)
    }

    fun postComplaintBase(hashMap: HashMap<String, String>) {
        isLoading.postValue(true)
        mConnection.postComplaintApi(map, hashMap).enqueue(baseCalback as Callback<BaseResponse>)
    }

    fun getFAQListBase() {
        isLoading.postValue(true)
        mConnection.getFAQListApi(map).enqueue(baseCalback as Callback<BaseResponse>)
    }

    fun getSOSListBase() {
        isLoading.postValue(true)
        mConnection.getSOSListApi(map).enqueue(baseCalback as Callback<BaseResponse>)
    }

    fun driverStatus() {
        isLoading.postValue(true)
        mConnection.driverStatus(map).enqueue(baseCalback as Callback<BaseResponse>)
    }

    fun respondApi(hashMap: HashMap<String, String>) {
        isLoading.postValue(true)
        mConnection.requestRespondApi(map, hashMap).enqueue(baseCalback as Callback<BaseResponse>)
    }

    fun arriveApi(hashMap: HashMap<String, String>) {
        isLoading.postValue(true)
        mConnection.requestArrived(map, hashMap).enqueue(baseCalback as Callback<BaseResponse>)
    }


    fun startTripBase() {
        isLoading.postValue(true)
        mConnection.requestStart(map, requestbody).enqueue(baseCalback as Callback<BaseResponse>)
    }

    fun startWIthImageApi() {
        isLoading.postValue(true)
        mConnection.requestStatWithImage(map, requestbody,body!!).enqueue(baseCalback as Callback<BaseResponse>)
    }


    fun endTripBase() {
        isLoading.postValue(true)
        mConnection.requestEnd(map, requestbody).enqueue(baseCalback as Callback<BaseResponse>)
    }

    fun endTripWithImageBase() {
        isLoading.postValue(true)
        mConnection.requestEndWithImage(map, requestbody,body!!).enqueue(baseCalback as Callback<BaseResponse>)
    }

    fun requestCancelList() {
        isLoading.postValue(true)
        mConnection.requestCancelList(map).enqueue(baseCalback as Callback<BaseResponse>)
    }

    fun submitCancelation(hashMap: HashMap<String, String>) {
        isLoading.postValue(true)
        mConnection.updateCancel(map, hashMap).enqueue(baseCalback as Callback<BaseResponse>)
    }

    fun getHistoryBase(hashMap: HashMap<String, String>, pageNo: String) {
        mConnection.getHistoryListApi(map, hashMap, pageNo)
            .enqueue(baseCalback as Callback<BaseResponse>)
    }

    fun getLogOutApiBase() {
        isLoading.value = true
        mConnection.getLogOutApi(map).enqueue(baseCalback as Callback<BaseResponse>)
    }

    fun getWalletListApiBase() {
        isLoading.postValue(true)
        mConnection.getWalletListApi(map).enqueue(baseCalback as Callback<BaseResponse>)
    }

    fun getWalletAddAmountBase(hashMap: HashMap<String, String>) {
        mConnection.getWalletAddAmount(map, hashMap).enqueue(baseCalback as Callback<BaseResponse>)
    }

    fun rateUserBase(hashMap: HashMap<String, String>) {
        isLoading.postValue(true)
        mConnection.rateUserApi(map, hashMap).enqueue(baseCalback as Callback<BaseResponse>)
    }


    fun saveSosBase(hashMap: HashMap<String, String>) {
        isLoading.postValue(true)
        mConnection.saveSosApi(map, hashMap).enqueue(baseCalback as Callback<BaseResponse>)
    }

    fun deleteSosBase(id: String) {
        isLoading.postValue(true)
        mConnection.deleteSosApi(map, id).enqueue(baseCalback as Callback<BaseResponse>)
    }

    fun getDashDetailsBase() {
        isLoading.postValue(true)
        mConnection.getDashDetailsApi(map).enqueue(baseCalback as Callback<BaseResponse>)
    }

    fun getSubscriptionListBase() {
        isLoading.postValue(true)
        mConnection.getSubscriptionList(map).enqueue(baseCalback as Callback<BaseResponse>)
    }

    fun subscriptionAdd(hashMap: HashMap<String, String>) {
        isLoading.postValue(true)
        mConnection.subscriptionAdd(map, hashMap).enqueue(baseCalback as Callback<BaseResponse>)
    }

    fun locationApprove(hashMap: HashMap<String, String>) {
        isLoading.postValue(true)
        mConnection.locationApprove(map, hashMap).enqueue(baseCalback as Callback<BaseResponse>)
    }


    fun getNotifactionBase(pageNo: String) {
        isLoading.postValue(true)
        mConnection.getNotificationApi(map, pageNo).enqueue(baseCalback as Callback<BaseResponse>)
    }

    fun createRequestBase(hashMap: HashMap<String, String>) {
        isLoading.postValue(true)
        mConnection.createRequestApi(map, hashMap).enqueue(baseCalback as Callback<BaseResponse>)
    }

    fun getCompanyListBase() {
        isLoading.postValue(true)
        mConnection.getCompanyListsApi(map).enqueue(baseCalback as Callback<BaseResponse>)
    }

    fun getVehicleModelsBase(hashMap: HashMap<String, String>) {
        isLoading.postValue(true)
        mConnection.getVehicleModelsApi(map, hashMap).enqueue(baseCalback as Callback<BaseResponse>)
    }

    fun sendCustomOtp(hashMap: HashMap<String, String>) {
        isLoading.postValue(true)
        mConnection.sendCustomOtp(map, hashMap)
            .enqueue(baseCalback as Callback<BaseResponse>)
    }

    fun getPostSelectedLanguage(hashMap: HashMap<String, String>) {
        isLoading.postValue(true)
        mConnection.getPostSelectedLanguageBase(map, hashMap)
            .enqueue(baseCalback as Callback<BaseResponse>)
    }

    fun getDriverReferal() {
        isLoading.postValue(true)
        mConnection.getDriverReferalApi(map).enqueue(baseCalback as Callback<BaseResponse>)
    }

    fun getSingleHistoryBase(hashMap: HashMap<String, String>){
        isLoading.postValue(true)
        mConnection.getSingleHistory(map,hashMap).enqueue(baseCalback as Callback<BaseResponse>)
    }

    fun uploadNightImageBase(){
        isLoading.postValue(true)
        mConnection.updateNightImage(map, requestbody,body!!)
            .enqueue(baseCalback as Callback<BaseResponse>)
    }

    fun skipNightImgBase(fMap: HashMap<String,String>){
        isLoading.postValue(true)
        mConnection.skipNightImgApi(map,fMap).enqueue(baseCalback as Callback<BaseResponse>)
    }

    fun requestRetakeBase(fMap: HashMap<String,String>){
        isLoading.postValue(true)
        mConnection.requestRetakeApi(map,fMap).enqueue(baseCalback as Callback<BaseResponse>)
    }

    fun uploadNightBothBase( driver :MultipartBody.Part , user :  MultipartBody.Part){
        isLoading.postValue(true)
        mConnection.uploadNightBoth(map, requestbody,driver,user)
            .enqueue(baseCalback as Callback<BaseResponse>)
    }


    fun orderApi(hashMap: HashMap<String, String>) {
        isLoading.postValue(true)
        mConnection.getOrderApi(map, hashMap).enqueue(baseCalback as Callback<BaseResponse>)
    }



    private val baseCalback = object : Callback<T?> {
        override fun onFailure(call: Call<T?>?, t: Throwable?) {
            onFailureApi(call, CustomException(501, t!!.localizedMessage))
        }

        override fun onResponse(call: Call<T?>?, response: Response<T?>?) {
//            response?.isSuccessful.let {
//                if (response!!.code() == 200)
//                    onSuccessfulApi(response.body())
//                else if (response.errorBody() != null && response.errorBody().toString() != null)
//                    onFailure(call, Throwable(response.message()))
//            }


            response?.isSuccessful.let {
                when {
                    response!!.code() == 200 -> onSuccessfulApi(response.body())
                    response.code() == 426 -> {
                        val errorMsg = CustomException(
                            response.code(),
                            "Update App"
                        )
                        onFailureApi(call, errorMsg)
                    }
                    response.errorBody() != null -> {
                        var errorMsg: CustomException? =
                            convertToException(response.errorBody()!!, response.code())
                        if (errorMsg?.exception == null) {
                            errorMsg = CustomException(response.code(), response.message())
                        }
                        onFailureApi(call, errorMsg)
                    }
                    else -> {
                        val errorMsg = CustomException(
                            response.code(),
                            "Something went wrong. Please try again."
                        )
                        onFailureApi(call, errorMsg)
                    }
                }

            }
        }
    }

    fun setNavigator(navigator: N) {
        mNavigator = navigator
    }

    fun getNavigator() = mNavigator

    open fun round(value: Double, precision: Int): Double {
        val scale = 10.0.pow(precision.toDouble()).toInt()
        return (value * scale).roundToInt().toDouble() / scale
    }
    fun changeMapStyle(googleMap: GoogleMap?, context: Context) {
        if (googleMap == null) return
        val style = MapStyleOptions.loadRawResourceStyle(context, R.raw.style_json)
        googleMap.setMapStyle(style)
    }

    fun setLanguage(c: Context, lang: String) {
        val localeNew = Locale(lang)
        Locale.setDefault(localeNew)
        val res: Resources = c.resources
        val newConfig = Configuration(res.configuration)
        newConfig.setLocale(localeNew)
        newConfig.setLayoutDirection(localeNew)
        res.updateConfiguration(newConfig, res.displayMetrics)
        newConfig.setLocale(localeNew)
        c.createConfigurationContext(newConfig)
    }


}