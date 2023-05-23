package com.rodaClone.driver.drawer.searchPlace

import android.app.Application
import android.text.Editable
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.google.android.gms.maps.model.LatLng
import com.google.gson.JsonObject
import com.rodaClone.driver.base.BaseVM
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.connection.ConnectionHelper
import com.rodaClone.driver.connection.CustomException
import com.rodaClone.driver.connection.FavPlace
import com.rodaClone.driver.ut.MapsHelper
import com.rodaClone.driver.ut.SessionMaintainence
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class SearchPlaceVm @Inject constructor(
    val application: Application,
    val session: SessionMaintainence,
    val mConnect: ConnectionHelper,
    private val mapsHelper: MapsHelper
) :
    BaseVM<BaseResponse, SearchPlaceNavigator>(session, mConnect) {
    var updatedLatLng = ObservableField<LatLng>()
    var favPlacesVM: MutableList<FavPlace.Favourite> = ArrayList()
    val dropAddress = ObservableField("")
    val searchCondition: IntArray = intArrayOf(4, 6, 8, 10)
    val showClose = ObservableBoolean(false)
    val addressTodis = ObservableField("")
    val showRecylers = ObservableBoolean(false)
    override fun onSuccessfulApi(response: BaseResponse?) {

    }

    override fun onFailureApi(call: Call<BaseResponse?>?, t: CustomException?) {

    }

    private fun getPlaces(text: String) {
        favPlacesVM.clear()
        val placesApi: Callback<*> = object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful && response.body()!!["status"].asString.equals(
                        "OK",
                        ignoreCase = true
                    )
                ) {
                    val predictionJsonArray = response.body()!!.getAsJsonArray("predictions")
                    printE("predictionList", predictionJsonArray.toString())
                    for (i in 0 until predictionJsonArray.size()) {
                        val favplace = FavPlace.Favourite()
                        val address = predictionJsonArray[i].asJsonObject["description"].asString
                        favplace.address = address
                        if (address.contains(",")) {
                            favplace.title = address.substring(0, address.indexOf(","))
                        } else if (!address.contains(",")) {
                            favplace.title = address
                        }
                        favPlacesVM.add(favplace)
                    }
                    getNavigator().addList(favPlacesVM)
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println("++$t")
            }
        }

//        val loacation = Location(LocationManager.GPS_PROVIDER)
//        loacation.latitude = session.getString(SessionMaintainence.CURRENT_LATITUDE)!!.toDouble()
//        loacation.latitude = session.getString(SessionMaintainence.CURRENT_LONGITUDE)!!.toDouble()

        val location: String =
            session.getString(SessionMaintainence.CURRENT_LATITUDE) + "," + session.getString(
                SessionMaintainence.CURRENT_LONGITUDE
            )
        printE("location---", "__$location")
        mapsHelper.GetPlaceApi(
            location, text, false, session.getString(SessionMaintainence.PLACE_API_DYNAMIC_KEY)
        )?.enqueue(placesApi as Callback<JsonObject?>)
    }

    fun dropTextChanged(editable: Editable) {
        if (editable.length > 1)
            showClose.set(true)
        else
            showClose.set(false)
        if (!(editable.isEmpty() || editable.length < 3)) {
            if (editable.length in searchCondition) {
                printE("editableStrring", editable.toString())
                getPlaces(editable.toString())
            }
        }
    }

    fun getLatLngFromAddress(address: String) {
        isLoading.postValue(true)
        mapsHelper.GetLatLngFromAddress(
            address,
            false, session.getString(SessionMaintainence.GEOCODE_DYNAMIC_KEY)
        )!!.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                isLoading.postValue(false)
                if (response.isSuccessful) {
                    if (response.body() != null && response.body()!!
                            .getAsJsonArray("results") != null && response.body()!!
                            .getAsJsonArray("results").size() != 0
                    ) {
                        val lat = response.body()!!
                            .getAsJsonArray("results")[0].asJsonObject["geometry"].asJsonObject["location"].asJsonObject["lat"].asDouble
                        val lng = response.body()!!
                            .getAsJsonArray("results")[0].asJsonObject["geometry"].asJsonObject["location"].asJsonObject["lng"].asDouble
                        updatedLatLng.set(LatLng(lat, lng))
                        getNavigator().goToInstantTrip()

                    }
                } else {
//                        Log.d(
//                            TAG,
//                            "GetAddressFromLatlng$response"
//                        )
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                isLoading.postValue(false)
//                    Log.d(
//                        TAG,
//                        "GetAddressFromLatlng$t"
//                    )
            }
        })
    }

    fun onCloseClicked(view: View) {
        addressTodis.set("")
        showRecylers.set(false)

    }


    fun backPressed(view: View) {
        getNavigator().backPressed()
    }

}