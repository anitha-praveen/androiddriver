package com.rodaClone.driver.drawer.mapfragment

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.gson.Gson
import com.rodaClone.driver.R
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.base.BaseVM
import com.rodaClone.driver.connection.ConnectionHelper
import com.rodaClone.driver.connection.CustomException
import com.rodaClone.driver.ut.SessionMaintainence
import retrofit2.Call
import javax.inject.Inject

import androidx.databinding.ObservableField
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.*


class MapFragmentVM @Inject constructor(
    val application: Application,
    val session: SessionMaintainence,
    val mConnect: ConnectionHelper
) :
    BaseVM<BaseResponse, MapFragmentNavigator>(session, mConnect) {

    lateinit var fusedLocationClient: FusedLocationProviderClient
    var currLatLng: MutableLiveData<LatLng>? = MutableLiveData()
    var googleMap: GoogleMap? = null
    private var driverMarker: Marker? = null
    var setToglleStatus = ObservableBoolean(true)
    lateinit var driverStatus: BaseResponse.OnlineOfflineData
    var showRemainingDays =
        ObservableBoolean(false) /* decide whether to show remaining days of subscription if subscribe */
    var remainingDays = ObservableField("") /* remaining days of subscription if subscribe */
    val typeSlug = ObservableField("")

    //    public var GotTranslations: MutableLiveData<String> = MutableLiveData()
    override fun onSuccessfulApi(response: BaseResponse?) {
        isLoading.value = false
//        Log.d("keyssss", "Susscess")
        if (response?.message != null) {
            driverStatus = Gson().fromJson(
                Gson().toJson(response.data),
                BaseResponse.OnlineOfflineData::class.java
            )
            setToglleStatus.set(driverStatus.onlineStatus == 1)
        }

    }

    override fun onFailureApi(call: Call<BaseResponse?>?, t: CustomException?) {
//        Log.d("keyssss", "onFailureAPI")
        isLoading.value = false
        getNavigator().showMessage(t!!.exception!!)
    }

    fun getLastLocation(context: Context, googleMap: GoogleMap) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                session.saveString(SessionMaintainence.CURRENT_LATITUDE, "" + location.latitude)
                session.saveString(
                    SessionMaintainence.CURRENT_LONGITUDE,
                    "" + location.longitude
                )
                getNavigator().startOnetimeReq(location)
                currLatLng!!.value = LatLng(location.latitude, location.longitude)
                //getAddressFromLatLng()
                markerAnimation(googleMap, currLatLng!!.value, 16.0f)
            } else {
                getNavigator().getAct()?.let { requestLocationUpdates(it) }
            }

        }
    }


    fun markerAnimation(googleMap: GoogleMap, latLng: LatLng?, zoomLevel: Float) {
        googleMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(latLng!!, zoomLevel)
        )
        val bitmapDescriptorFactory =
            BitmapDescriptorFactory.fromResource(R.drawable.car_icon_new)
        if (driverMarker != null)
            driverMarker!!
                .remove()


        driverMarker =
            googleMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .icon(bitmapDescriptorFactory)
            )


    }


    fun focusCurrentLocation(v: View) {
        getLastLocation(v.context, googleMap!!)
    }

    fun toggleStatus() {
        if (getNavigator().isNetworkConnected()) {
            driverStatus()
        } else getNavigator().showNetworkUnAvailable()
    }

    fun openInstantTrip() {
        if (session.getBoolean(SessionMaintainence.DriverStatus))
            getNavigator().openInstantTrip()
        else
            getNavigator().showMessage(translationModel?.txt_change_online ?: "")
    }


    fun openSos(v: View) {
        getNavigator().openSos()
    }

    var mLocationRequest: LocationRequest? = null
    fun requestLocationUpdates(requireActivity: FragmentActivity) {
        mLocationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 10000
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

    var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                if (location != null) {
                    currLatLng?.value = (LatLng(location.latitude, location.longitude))
                    getNavigator().startOnetimeReq(location)
                    //  getNavigator().startService(location)
                }
            }

            session.saveString(
                SessionMaintainence.CURRENT_LATITUDE,
                "" + currLatLng?.value!!.latitude
            )
            session.saveString(
                SessionMaintainence.CURRENT_LONGITUDE,
                "" + currLatLng?.value!!.longitude
            )
            markerAnimation(googleMap!!, currLatLng!!.value, 16.0f)
            //removeLocationCallBack()
        }
    }

    fun removeLocationCallBack() {
        fusedLocationClient.removeLocationUpdates(mLocationCallback)
    }


}