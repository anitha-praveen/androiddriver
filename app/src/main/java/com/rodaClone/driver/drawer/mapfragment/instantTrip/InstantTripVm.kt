package com.rodaClone.driver.drawer.mapfragment.instantTrip

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.rodaClone.driver.base.BaseVM
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.connection.ConnectionHelper
import com.rodaClone.driver.connection.CustomException
import com.rodaClone.driver.ut.Config
import com.rodaClone.driver.ut.MapsHelper
import com.rodaClone.driver.ut.SessionMaintainence
import com.rodaClone.driver.ut.Utilz
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class InstantTripVm @Inject constructor(
    val application: Application,
    val session: SessionMaintainence,
    val mConnect: ConnectionHelper,
    private val mapsHelper: MapsHelper
) : BaseVM<BaseResponse, InstantTripNavigator>(session, mConnect) {
    lateinit var fusedLocationClient: FusedLocationProviderClient
    val zoom = 19f
    var vmPickupLatLng = ObservableField<LatLng>()
    val isDropPressed = ObservableBoolean(false)
    val dropLatLng = ObservableField<LatLng>()
    var dropAddress = ObservableField("")
    val showPickupText = ObservableBoolean(true)
    val showDropText = ObservableBoolean(true)

    val showButton = ObservableBoolean(false)
    var currLatLng: MutableLiveData<LatLng>? = MutableLiveData()
    lateinit var googleMap: GoogleMap
    var isPicAdresspressed = ObservableBoolean(false)
    var pickupAddress = ObservableField("")
    private val typeId = session.getString(SessionMaintainence.TYPE_ID)
    val userPhoneNumber = ObservableField("")

    val showMarker = ObservableBoolean(false)
    override fun onSuccessfulApi(response: BaseResponse?) {
        if (response != null) {
            val data: BaseResponse.DataObjectsAllApi =
                Gson().fromJson(
                    Gson().toJson(response.data),
                    BaseResponse.DataObjectsAllApi::class.java
                )
            session.saveString(SessionMaintainence.ReqID, data.requestData?.id!!)
            session.saveReqTripData(
                SessionMaintainence.ReqTripData,
                Gson().toJson(response.data)
            )
            session.saveString(SessionMaintainence.IS_TRIP_STARTED, "TRUE")
            session.saveString(SessionMaintainence.SAVED_DISTANCE_TRAVELLED, "")
            session.saveString(SessionMaintainence.SAVED_TRIP_LAT, "")
            session.saveString(SessionMaintainence.SAVED_TRIP_LNG, "")
            Utilz.updateDriverTripStatus(2, data.requestData.id)
            if (data.requestData.is_instant_trip == 1) {
                getNavigator().openTrip(data)
            }
        }
    }

    override fun onFailureApi(call: Call<BaseResponse?>?, t: CustomException?) {
        isLoading.value = false
        showButton.set(false)
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

                currLatLng!!.value = LatLng(location.latitude, location.longitude)
                getAddressFromLatLng()
            }
        }
    }

    fun moveCamera(googleMap: GoogleMap, latLng: LatLng?, zoomLevel: Float) {
        googleMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(latLng!!, zoomLevel)
        )
    }

    fun getAddressFromLatLng() {
        var swapingLatLong: LatLng? = null
        if (!isDropPressed.get()) {
            swapingLatLong = LatLng(
                vmPickupLatLng.get()!!.latitude,
                vmPickupLatLng.get()!!.longitude
            )
        } else {
            swapingLatLong = LatLng(
                dropLatLng.get()!!.latitude,
                dropLatLng.get()!!.longitude
            )
        }
        mapsHelper.GetAddressFromLatLng(
            swapingLatLong.latitude.toString() + "," + swapingLatLong.longitude,
            false, session.getString(SessionMaintainence.GEOCODE_DYNAMIC_KEY)
        )!!.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.isSuccessful) {
                    if (response.body() != null && response.body()!!
                            .getAsJsonArray("results") != null
                    ) {
                        val status = response.body()!!["status"].asString
                        if (status == "OK") {
                            if (!isDropPressed.get()) {
                                pickupAddress.set(
                                    response.body()!!
                                        .getAsJsonArray("results")[0].asJsonObject["formatted_address"].asString
                                )
                            } else {
                                dropAddress.set(
                                    response.body()!!
                                        .getAsJsonArray("results")[0].asJsonObject["formatted_address"].asString
                                )
                            }
                        } else if (status == "OVER_QUERY_LIMIT") {
                            try {
                                val geocoder = getNavigator().getCtx().let { Geocoder(it) }
                                var mAddress = ""
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    geocoder.getFromLocation(
                                        swapingLatLong.latitude,
                                        swapingLatLong.longitude,
                                        1
                                    ) { addresses ->
                                        if (addresses.size > 0) {
                                            if (addresses[0].getAddressLine(0) != null) {
                                                mAddress = addresses[0].getAddressLine(0)
                                            }
                                            if (addresses[0].getAddressLine(1) != null) {
                                                mAddress =
                                                    mAddress + ", " + addresses[0].getAddressLine(1)
                                            }
                                            if (addresses[0].getAddressLine(2) != null) {
                                                mAddress =
                                                    mAddress + ", " + addresses[0].getAddressLine(2)
                                            }
                                        }
                                        if (!isDropPressed.get()) {
                                            pickupAddress.set(mAddress)
                                        } else {
                                            dropAddress.set(mAddress)
                                        }

                                    }
                                } else {
                                    val addresses = geocoder.getFromLocation(
                                        swapingLatLong.latitude,
                                        swapingLatLong.longitude,
                                        1
                                    )
                                    if (addresses != null && addresses.size > 0) {
                                        if (addresses[0].getAddressLine(0) != null) {
                                            mAddress = addresses[0].getAddressLine(0)
                                        }
                                        if (addresses[0].getAddressLine(1) != null) {
                                            mAddress =
                                                mAddress + ", " + addresses[0].getAddressLine(1)
                                        }
                                        if (addresses[0].getAddressLine(2) != null) {
                                            mAddress =
                                                mAddress + ", " + addresses[0].getAddressLine(2)
                                        }
                                    }
                                    if (!isDropPressed.get()) {
                                        pickupAddress.set(mAddress)
                                    } else {
                                        dropAddress.set(mAddress)
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {

            }
        })
    }

    fun dropTextPressed() {
        if (isPicAdresspressed.get()) {
            moveCamera(googleMap, dropLatLng.get(), zoom)
            isDropPressed.set(true)
            isPicAdresspressed.set(false)
        } else {
            getNavigator().openSearchPlace()
        }
    }

    fun pickTextPressed() {
        if (isDropPressed.get()) {
            getNavigator().pickAddressPressed()
        }
    }

    fun currentLoaction(view: View) {
        getNavigator().currentLocation()
    }

    fun confirm(view: View) {
        getNavigator().showDialog()

    }

    fun createRequest() {
        val hashMap = HashMap<String, String>()
        hashMap.clear()
        printE("picupAddresss", pickupAddress.get()!!)
        hashMap[Config.pick_lat] = vmPickupLatLng.get()!!.latitude.toString()
        hashMap[Config.pick_lng] = vmPickupLatLng.get()!!.longitude.toString()
        hashMap["pick_address"] = pickupAddress.get()!!
        hashMap["ride_type"] = "LOCAL"
        if (!(dropLatLng.get() == null)) {
            hashMap[Config.drop_lat] = dropLatLng.get()!!.latitude.toString()
            hashMap[Config.drop_lng] = dropLatLng.get()!!.longitude.toString()
            hashMap[Config.drop_address] = dropAddress.get()!!
        }
        hashMap[Config.phone_number] = userPhoneNumber.get().toString()
        hashMap["vehicle_type"] = typeId.toString()
        hashMap["is_instant"] = 1.toString()
        hashMap[Config.payment_opt] = "CASH"
        createRequestBase(hashMap)
    }

    var requestRef: DatabaseReference? = null

    init {
        var database = FirebaseDatabase.getInstance()

        requestRef = database.getReference("requests")
    }

}