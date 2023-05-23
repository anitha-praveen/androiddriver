package com.rodaClone.driver.loginSignup.login

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import androidx.databinding.ObservableField
import com.google.gson.Gson
import com.rodaClone.driver.base.BaseVM
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.connection.ConnectionHelper
import com.rodaClone.driver.ut.SessionMaintainence
import retrofit2.Call
import javax.inject.Inject
import android.location.Geocoder
import android.os.Looper
import androidx.core.content.ContextCompat
import androidx.databinding.ObservableBoolean
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.rodaClone.driver.R
import com.rodaClone.driver.connection.CustomException
import com.rodaClone.driver.connection.responseModels.Country
import java.io.IOException
import java.util.*


class LoginVM @Inject constructor(
    val session: SessionMaintainence,
    val mConnect: ConnectionHelper

) :
    BaseVM<BaseResponse, LoginNavigator>(session, mConnect) {
    var countryCode = ObservableField("+")
    lateinit var countryModel: Country

    /*BaseView baseView;*/
    var emailOrPhone = ObservableField("")
    val gson = Gson()
    var isTamilLanguage = ObservableBoolean(false)
    var termsCheckText = ObservableField("")
    override fun onSuccessfulApi(response: BaseResponse?) {
        isLoading.value = false
    }

    override fun onFailureApi(call: Call<BaseResponse?>?, t: CustomException?) {
        isLoading.value = false
        getNavigator().showMessage(t!!.exception!!)
    }

    fun onClickOutSide() {
        getNavigator().hideKeyBoard()
    }

    fun countryChoose() {
        getNavigator().openCountryListDialogue()
    }

    fun onClickConfirm() {
        when {
            emailOrPhone.get().isNullOrEmpty() -> getNavigator().showMessage(translationModel?.txt_phone_cannot_be_empty ?: "")
            emailOrPhone.get()!!.length < 4  -> getNavigator().showMessage(getNavigator().getContextAttached().getString(
                R.string.enter_ten_dig_num))
            else -> getNavigator(). openOtp(emailOrPhone.get()!!, countryCode.get()!!, countryModel.id!!)
        }
    }

    fun getCountryFromLatLng(lat: Double, lng: Double): String {
        var countryName = ""
        val geocoder = Geocoder(getNavigator().getContextAttached(), Locale.getDefault())
        try {
            val addresses: List<Address> = geocoder.getFromLocation(lat, lng, 1) as List<Address>
            val obj = addresses[0]
            countryName = obj.countryCode
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return countryName
    }

    fun checkCountryModelIsInitialized(): Boolean {
        return this::countryModel.isInitialized
    }


    var mLocationRequest: LocationRequest? = null
    lateinit var fusedLocationClient: FusedLocationProviderClient

    fun requestLocationUpdates(requireActivity: FragmentActivity) {
        mLocationRequest = LocationRequest.create().apply {
            interval = 1000
            fastestInterval = 1000
            isWaitForAccurateLocation = true
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            maxWaitTime = 100
        }
        if (ContextCompat.checkSelfPermission(
                requireActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(
                mLocationRequest!!,
                mLocationCallback,
                Looper.myLooper()!!
            )
        }
    }

    var currentLatlng = ObservableField<LatLng>()
     var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                if (location != null) {
                    currentLatlng.set(LatLng(location.latitude, location.longitude))
                }
            }
            session.saveString(SessionMaintainence.CURRENT_LATITUDE, "" + currentLatlng.get()!!.latitude)
            session.saveString(
                SessionMaintainence.CURRENT_LONGITUDE,
                "" + currentLatlng.get()!!.longitude
            )
            removeLocationCallBack()
        }
    }

    fun removeLocationCallBack(){
        fusedLocationClient.removeLocationUpdates(mLocationCallback)
    }

}