package com.rodaClone.driver.splash

import com.google.gson.Gson
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.base.BaseVM
import com.rodaClone.driver.connection.ConnectionHelper
import com.rodaClone.driver.connection.CustomException
import com.rodaClone.driver.connection.responseModels.AvailableCountryAndKLang
import com.rodaClone.driver.connection.responseModels.S3Model
import com.rodaClone.driver.ut.Config
import com.rodaClone.driver.ut.SessionMaintainence
import retrofit2.Call
import javax.inject.Inject

class SplashVM @Inject constructor(
    val session: SessionMaintainence,
    val mConnect: ConnectionHelper
) :
    BaseVM<BaseResponse, SplashNavigator>(session, mConnect) {
    var map: HashMap<String, String> = HashMap()
    lateinit var availableCountryAndKLang: AvailableCountryAndKLang
    var taskId = -1
    val gson = Gson()
    override fun onSuccessfulApi(response: BaseResponse?) {
        isLoading.value = false
        if (response!!.success == true) {
            if (taskId == 0) {
                val res = Gson().fromJson(Gson().toJson(response.data), S3Model::class.java)
                res?.s3BucketKeyId?.let {
                    session.saveString(
                        SessionMaintainence.AWS_ACCESS_KEY_ID,
                        it
                    )
                }
                res?.s3BucketSecretAccessKey?.let {
                    session.saveString(
                        SessionMaintainence.AWS_SECRET_ACCESS_KEY,
                        it
                    )
                }
                res?.s3BucketDefaultRegion?.let {
                    session.saveString(
                        SessionMaintainence.AWS_DEFAULT_REGION,
                        it
                    )
                }
                res?.s3BucketName?.let {
                    session.saveString(
                        SessionMaintainence.AWS_BUCKET,
                        it
                    )
                }
                res.places_api_key?.let {
                    session.saveString(
                        SessionMaintainence.PLACE_API_DYNAMIC_KEY,
                        it
                    )
                }
                res.distance_api_key?.let {
                    session.saveString(
                        SessionMaintainence.DISTANCE_DYNAMIC_KEY,
                        it
                    )
                }
                res.geo_coder_api_key?.let {
                    session.saveString(
                        SessionMaintainence.GEOCODE_DYNAMIC_KEY,
                        it
                    )
                }
                res.directional_api_key?.let {
                    session.saveString(
                        SessionMaintainence.DIRECTION_DYNAMIC_KEY,
                        it
                    )
                }
                session.saveAvailableCountryAndLanguages(response.data)
                getNavigator().translationForCurrentLanguage(session.getAvailableCountryAndLanguages()!!)
            } else if (taskId == 1) {
                session.saveString(
                    SessionMaintainence.TRANSLATED_DATA,
                    gson.toJson(response.data!!)
                )
                getNavigator().startRequestingPermission()
            }
        }
    }

    override fun onFailureApi(call: Call<BaseResponse?>?, t: CustomException?) {
        isLoading.value = false
        if (t?.code == 426) {
            getNavigator().openAppUpdateDialog()
        } else
            getNavigator().showMessage(t!!.exception!!)
    }

    fun getLanguageCodeApi() {
        taskId = 0
        if (getNavigator().isNetworkConnected()) {
            map.clear()
            map[Config.Code] = Config.CodeValue
            getLanguageBase(map)
        } else getNavigator().showNetworkUnAvailable()
    }

}