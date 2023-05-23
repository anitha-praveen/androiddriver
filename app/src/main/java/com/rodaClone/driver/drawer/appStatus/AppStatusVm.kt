package com.rodaClone.driver.drawer.appStatus

import android.app.Application
import android.content.Context
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.PowerManager
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.google.firebase.database.*
import com.google.gson.Gson
import com.rodaClone.driver.base.BaseVM
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.connection.ConnectionHelper
import com.rodaClone.driver.connection.CustomException
import com.rodaClone.driver.ut.SessionMaintainence
import dagger.android.support.DaggerAppCompatActivity
import retrofit2.Call
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class AppStatusVm @Inject constructor(
    val application: Application,
    val session: SessionMaintainence,
    val mConnect: ConnectionHelper
) :
    BaseVM<BaseResponse, AppStatusNavigator>(session, mConnect) {

    val networkStatus = ObservableField("")

    val dateTxt = ObservableField("")

    val isNetworkStatusOk = ObservableBoolean(false)
    val isUpdatedOk = ObservableBoolean(false)
    val lastUpdatedTxt = ObservableField("")
    val isGpsOk = ObservableBoolean(false)
    val isBatteryOptimized = ObservableBoolean(false)
    val isAppStatusOk = ObservableBoolean(false)

    val locationSerivesTxt = ObservableField("")
    val batteryOptimizationTxt = ObservableField("")
    val appStatusTxt = ObservableField("")
    var ref: DatabaseReference? = null
    var showAutoStart = ObservableBoolean(false)
    val showLastUpdatedTxt = ObservableField(false)

    private var locationManager: LocationManager? = null
    private var isGPSEnabled: Boolean = false
    private var isNetworkEnabled: Boolean = false
    var appVersion = ObservableField("")

    override fun onSuccessfulApi(response: BaseResponse?) {

    }

    override fun onFailureApi(call: Call<BaseResponse?>?, t: CustomException?) {

    }

    fun getUpdatedMills() {

        if (session.getString(SessionMaintainence.DriverData)!!.isNotEmpty()) {
            val driverDataaa = Gson().fromJson(
                session.getString(SessionMaintainence.DriverData),
                BaseResponse.ReqDriverData::class.java
            )
            printE("driverSlug", driverDataaa.slug.toString())
            ref!!.child("${driverDataaa.slug}").child("updated_at").addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val value = snapshot.value.toString().toLongOrNull()
                    value?.let {
                        showLastUpdatedTxt.set(true)
                        val currentMills = System.currentTimeMillis()
                        val timeDifference = currentMills - it
                        val fiveHoursInms = 18000000
                        val formatedDAte = getDate(it)
                        dateTxt.set(formatedDAte)
                        if (timeDifference >= fiveHoursInms) {

                            isUpdatedOk.set(false)
                            updateAppText()
                        } else {
                            isUpdatedOk.set(true)
                            updateAppText()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }

    }

    private fun getDate(milliSeconds: Long): String {
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat("MMM:dd:yyyy hh:mm:ss", Locale.ENGLISH)

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }

    fun getLocationAndNetwork(context: Context?) {
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nc = cm.getNetworkCapabilities(cm.activeNetwork)
        val downSpeed = nc?.linkDownstreamBandwidthKbps
        if (downSpeed != null) {
            if (downSpeed > 100) {
                networkStatus.set("${translationModel?.txt_network_status}: ${translationModel?.text_ok}")
                isNetworkStatusOk.set(true)
                updateAppText()
            } else {
                networkStatus.set("${translationModel?.txt_network_status}")
                isNetworkStatusOk.set(false)
                updateAppText()
            }
        }

        locationManager = context
            .getSystemService(Context.LOCATION_SERVICE) as LocationManager

        isGPSEnabled = locationManager!!
            .isProviderEnabled(LocationManager.GPS_PROVIDER)

        isNetworkEnabled = locationManager!!
            .isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (isGPSEnabled || isNetworkEnabled) {
            locationSerivesTxt.set("${translationModel?.txt_network_gps}: ${translationModel?.text_ok}")
            isGpsOk.set(true)
            updateAppText()
        } else {
            locationSerivesTxt.set("${translationModel?.txt_network_gps}")
            isGpsOk.set(false)
            updateAppText()
        }

    }

    fun checkBatteryOptimization(context: Context?) {
        val packageName = context!!.packageName
        val pm = context.getSystemService(DaggerAppCompatActivity.POWER_SERVICE) as PowerManager
        val batteryOptimizationRemoved = pm.isIgnoringBatteryOptimizations(packageName)
        if (!batteryOptimizationRemoved) {
            batteryOptimizationTxt.set("${translationModel?.txt_battery_optimization}")
            isBatteryOptimized.set(false)
            updateAppText()
        } else {
            batteryOptimizationTxt.set("${translationModel?.txt_battery_optimization}: ${translationModel?.text_ok}")
            isBatteryOptimized.set(true)
            updateAppText()
        }
    }

    fun openBatteryOptimization() {
        getNavigator().openBatteryOptimization()
    }

    fun openLocationSettings(view: View) {
        getNavigator().openLocationSetting()

    }

    fun updateAppText() {
        if (!isUpdatedOk.get() || !isGpsOk.get() || !isNetworkStatusOk.get() || !isBatteryOptimized.get()) {
            appStatusTxt.set(translationModel?.txt_not_fine)
            isAppStatusOk.set(false)
        } else {
            appStatusTxt.set(translationModel?.txt_working_fine)

            isAppStatusOk.set(true)
        }
    }

    fun onRefreshClick(view: View) {
        getNavigator().callReqinPro()
    }

    fun autoStartClick(view: View) {
        getNavigator().openAutoStart()
    }
}