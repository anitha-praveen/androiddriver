package com.rodaClone.driver.dialogs.cancel

import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.rodaClone.driver.base.BaseVM
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.connection.ConnectionHelper
import com.rodaClone.driver.connection.CustomException
import com.rodaClone.driver.connection.responseModels.RequestResponseData
import com.rodaClone.driver.ut.Config
import com.rodaClone.driver.ut.SessionMaintainence
import com.rodaClone.driver.ut.Utilz
import retrofit2.Call
import java.io.IOException
import java.util.*
import javax.inject.Inject

class CancelListVM @Inject constructor(
    val session: SessionMaintainence,

    val mConnect: ConnectionHelper

) : BaseVM<BaseResponse, CancelListNavigator>(session, mConnect) {

    var hashMap: HashMap<String, String>? = null
    var otherCancelReason = ObservableField<String>()
    var requestDataaa: RequestResponseData? = null
    var driverStatus: ObservableBoolean? = ObservableBoolean()
    var otherCancelAvaialability = ObservableBoolean(false)
    var data: BaseResponse.DataObjectsAllApi? = null
    var apiCallID: Int = -1
    var map: HashMap<String, String> = HashMap()
    private var reasonsList: List<BaseResponse.CancelReason>? = ArrayList()

    override fun onSuccessfulApi(response: BaseResponse?) {
        isLoading.value = false
        if (apiCallID == 1) {
            data = Gson().fromJson(
                Gson().toJson(response!!.data),
                BaseResponse.DataObjectsAllApi::class.java
            )
            reasonsList = data!!.reasons
            getNavigator().loadReasons(data!!.reasons)
        } else {
            Utilz.updateDriverTripStatus(
                4,
                requestDataaa?.id ?: session.getString(SessionMaintainence.ReqID)!!
            )
            session.saveString(SessionMaintainence.ReqID, "")
            session.saveString(SessionMaintainence.DRIVER_TO_PICKUP_POLY, "")
            session.saveBoolean(SessionMaintainence.IS_GRACE_TIME_COMPLETED, true)
            session.saveBoolean(SessionMaintainence.IS_GRACE_AFTER_START_COMPLETED,true)
            session.saveString(SessionMaintainence.SAVED_DISTANCE_TRAVELLED, "")
            session.saveString(SessionMaintainence.SAVED_TRIP_LAT, "")
            session.saveString(SessionMaintainence.SAVED_TRIP_LNG, "")
            val intent = Intent(Config.RECEIVE_CLEAR_DIST)
            LocalBroadcastManager.getInstance(getNavigator().getCtx()).sendBroadcast(intent)
            getNavigator().closeTripFragment()
        }

    }

    override fun onFailureApi(call: Call<BaseResponse?>?, t: CustomException?) {
        isLoading.value = false
        getNavigator().showMessage(t!!.exception!!)
    }

    fun onClickBack(view: View) {

    }

    fun onclickDontcancel(v: View) {
        getNavigator().dismissDialog()
    }

    fun onclickcancel(v: View) {
        if(getNavigator().getSelectedPosition() != -1)
        if (getNavigator().isNetworkConnected()) {
            apiCallID = 2
            map.clear()
            map[Config.request_id] = "" + requestDataaa!!.id
            map[Config.reason] = "" + reasonsList!![getNavigator().getSelectedPosition()].id
            map[Config.DRIVER_LAT] = session.getString(SessionMaintainence.CURRENT_LATITUDE)!!
            map[Config.DRIVER_LNG] = session.getString(SessionMaintainence.CURRENT_LONGITUDE)!!

            val latLng =
                LatLng(
                    session.getString(SessionMaintainence.CURRENT_LATITUDE)!!.toDouble(),
                    session.getString(SessionMaintainence.CURRENT_LONGITUDE)!!.toDouble()
                )
            getAddressFromLatLng(latLng, v.context)
            submitCancelation(map)
        } else getNavigator().showNetworkUnAvailable()

    }

    fun cancelationApi() {
        if (getNavigator().isNetworkConnected()) {
            apiCallID = 1
            requestCancelList()
        } else getNavigator().showNetworkUnAvailable()
    }

    private fun getAddressFromLatLng(latLng: LatLng?, context: Context) {
        try{
            val geocoder = Geocoder(context, Locale.getDefault())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(
                    latLng!!.latitude,
                    latLng.longitude,
                    1
                ) { addresses ->
                    if (addresses.isNotEmpty()) {
                        cancellationApiCall(context, addresses[0].getAddressLine(0))
                    } else {
                        isLoading.postValue(false)
                        getNavigator().showMessage("Unable to get current Address. Please try again")
                    }
                }
            } else {
                val addresses: List<Address> = geocoder.getFromLocation(
                    latLng!!.latitude,
                    latLng.longitude,
                    1
                ) as List<Address>
                if (addresses.isNotEmpty()) {
                    cancellationApiCall(context, addresses[0].getAddressLine(0))
                } else {
                    isLoading.postValue(false)
                    getNavigator().showMessage("Unable to get current Address. Please try again")
                }
            }
        }catch (e : IOException){
            ""
        }
    }

    fun cancellationApiCall(context: Context, address: String) {
        apiCallID = 2
        map.clear()
        map[Config.request_id] = "" + requestDataaa!!.id
        map[Config.reason] = "" + reasonsList!![getNavigator().getSelectedPosition()].id
        map[Config.DRIVER_LAT] = session.getString(SessionMaintainence.CURRENT_LATITUDE)!!
        map[Config.DRIVER_LNG] = session.getString(SessionMaintainence.CURRENT_LONGITUDE)!!
        map[Config.driver_location] = address
        submitCancelation(map)
    }
}