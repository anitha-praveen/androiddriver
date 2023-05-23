package com.rodaClone.driver.ut

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import com.rodaClone.driver.connection.responseModels.AvailableCountryAndKLang
import javax.inject.Inject

/**
 * You can edit shared preferences in a more secure way by calling the edit() method
 * on an EncryptedSharedPreferences object instead of on a SharedPreferences object
 * @sample https://developer.android.com/training/data-storage/shared-preferences#WriteSharedPreference
 * */
class SessionMaintainence @Inject constructor(activity: Context) {
    companion object {
        val ReqTripData: String = "ReqTripData"
        val DriverData: String = "DriverData"
        val LAST_REQ_ID: String = "-1"
        val DriverID: String = "DriverID"
        val DriverStatus: String = "DriverStatus"
        const val FCM_TOKEN = "fcm_token"
        const val AVAILABLE_COUNTRY_AND_LANGUAGES = "AVAILABLE_COUNTRY_AND_LANGUAGES"
        const val GetStartedScrnLoaded = "GetStartedScrnLoaded"
        const val TRANSLATED_DATA = "TRANSLATED_DATA"
        const val CURRENT_LANGUAGE = "CURRENT_LANGUAGE"
        const val TOUR_SHOWN = "TOUR_SHOWN"
        const val AccessToken = "AccessToken"
        const val TRANSLATION_TIME_OLD = "TRANSLATION_TIME_OLD"
        const val TRANSLATION_TIME_NOW = "TRANSLATION_TIME_NOW"
        const val CURRENT_LATITUDE = "CURRENT_LATITUDE"
        const val CURRENT_LONGITUDE = "CURRENT_LONGITUDE"
        const val BEARING = "BEARING"
        const val SelectedCountryCode = "SelectedCountryCode"
        const val ReqID = "ReqID"
        const val SAVED_WAITING_TIME =
            "SAVED_WAITING_TIME" /* used to store waiting time inside trip  (stored as integer)  */
        const val IS_GRACE_TIME_COMPLETED =
            "IS_GRACE_TIME_COMPLETED"  /*used to check whether grace time completed (stored as boolean) */
        const val SAVED_GRACE_TIME =
            "SAVED_GRACE_TIME" /* used to store completed grace time (stored as integer) */
        const val SAVED_DISTANCE_TRAVELLED =
            "SAVED_DISTANCE_TRAVELLED" /* used to store the distance travelled by the driver after trip started String */
        const val SAVED_TRIP_LAT =
            "SAVED_TRIP_LAT" /* used to store the last latitude obtained in the trip */
        const val SAVED_TRIP_LNG =
            "SAVED_TRIP_LNG" /* used to store the last longitude obtained in the trip */
        const val TYPE_ID = "type_id"
        const val DRIVER_TO_PICKUP_POLY =
            "DRIVER_TO_PICKUP_POLY" /* used to store driver location to pickup path */
        const val IS_TRIP_STARTED = "IS_TRIP_STARTED"
        const val ISSERVICESTARTED = "ISSERVICESTARTED"

        const val AWS_ACCESS_KEY_ID = "AWS_ACCESS_KEY_ID"
        const val AWS_SECRET_ACCESS_KEY = "AWS_SECRET_ACCESS_KEY"
        const val AWS_DEFAULT_REGION = "AWS_DEFAULT_REGION"
        const val AWS_BUCKET = "AWS_BUCKET"
        const val PLACE_API_DYNAMIC_KEY = "PLACE_API_DYNAMIC_KEY"
        const val DISTANCE_DYNAMIC_KEY = "DISTANCE_DYNAMIC_KEY"
        const val GEOCODE_DYNAMIC_KEY = "GEOCODE_DYNAMIC_KEY"
        const val DIRECTION_DYNAMIC_KEY = "DIRECTION_DYNAMIC_KEY"
        const val IS_GRACE_AFTER_START_COMPLETED = "IS_GRACE_AFTER_START_COMPLETED"
        const val SAVED_GRACE_TIME_AFTER_START = "SAVED_GRACE_TIME_AFTER_START"
        const val DISPLAY_WAITING = "DISPLAY_WAITING"
    }

    fun enterMID(mID: String) {
        with(sessionM.edit()) {
            putString(id_mid, mID)
            apply()
        }
    }

    fun getMID(): String? {
        return sessionM.getString(id_mid, "")
    }

    fun saveAvailableCountryAndLanguages(dataObj: Any?) {
        sessionM.edit().apply {
            putString(AVAILABLE_COUNTRY_AND_LANGUAGES, Gson().toJson(dataObj))
            apply()
        }
    }

    fun getAvailableCountryAndLanguages(): AvailableCountryAndKLang? {
        val result: AvailableCountryAndKLang?
        val sr = sessionM.getString(AVAILABLE_COUNTRY_AND_LANGUAGES, null)
        try {
            result = Gson().fromJson(sr, AvailableCountryAndKLang::class.java)
        } catch (ex: Exception) {
            return null
        }
        return result
    }

    fun saveString(key: String, str: String) {
        sessionM.edit().apply {
            putString(key, str)
            apply()
        }
    }

    fun getString(key: String?): String? {
        return sessionM.getString(key, "")
    }

    fun saveInt(key: String, int: Int) {
        sessionM.edit().apply {
            putInt(key, int)
            apply()
        }
    }


    fun getInt(key: String?): Int {
        return sessionM.getInt(key, 0)
    }

    fun saveLong(key: String, long: Long) {
        sessionM.edit().apply {
            putLong(key, long)
            apply()
        }
    }

    fun getLong(key: String): Long {
        return sessionM.getLong(key, 0)
    }

    fun saveBoolean(key: String, value: Boolean) {
        sessionM.edit().apply() {
            putBoolean(key, value)
            apply()
        }
    }

    fun getBoolean(key: String?): Boolean {
        return sessionM.getBoolean(key, false)
    }

    fun saveDriverData(driverData: String, toString: String) {
        sessionM.edit().apply() {
            putString(driverData, toString)
            apply()
        }
    }

    //
    fun getDriverData(driverData: String): String {
        return sessionM.getString(driverData, "")!!
    }

    fun saveReqTripData(reqTripData: String, toJson: String?) {
        sessionM.edit().apply() {
            putString(reqTripData, toJson)
            apply()
        }
    }

    fun clearTripDetails() {
        saveBoolean(IS_GRACE_TIME_COMPLETED, false)
        saveBoolean(IS_GRACE_AFTER_START_COMPLETED, false)
        saveString(IS_TRIP_STARTED, "FALSE")
        saveString(SAVED_TRIP_LAT, "")
        saveString(SAVED_TRIP_LNG, "")
        saveString(SAVED_DISTANCE_TRAVELLED, "")
    }

    private var sessionM: SharedPreferences
    val id_mid = "id_mid"
    val jid_transl = "jid_transl"

    init {
        val keyGenParameterSpec=
            MasterKey.Builder(activity).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
        sessionM = EncryptedSharedPreferences.create(activity,Config.sharedPrefName,keyGenParameterSpec,EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)
//        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
//        val masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)
//        sessionM = EncryptedSharedPreferences.create(
//            activity.getString(R.string.jid),
//            masterKeyAlias, activity,
//            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
//            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
//        )
    }
}