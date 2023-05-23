package com.rodaClone.driver.drawer.trip

import android.content.Context
import com.google.android.gms.maps.model.BitmapDescriptor
import com.rodaClone.driver.base.BaseViewOperator
import com.rodaClone.driver.connection.responseModels.RequestResponseData

interface TripNavigator : BaseViewOperator {
    fun getCtx(): Context?
    fun getMarkerIcon(icPickPin: Int): BitmapDescriptor?
    fun loadBill(data: RequestResponseData)
    fun openOtpDialog()
    fun openApproveAlert()
    fun loadRequestProgress()
    fun openSideMenu()
    fun openSos()
    fun loadProfile(toString: String)
    fun openMeterUploadDialog(mode:Int)
    fun showConfirmEndTripAlert()
    fun showTakePhotoDialog(isRetake : Boolean)
    fun showSkipOrReject(value : Boolean)
    fun openCancelReasons()
    fun uploadNightImageBoth()
    fun setUser(path : String)
    fun setDriver(path : String)
    fun closeUserDriverDialog()
    fun showImage(title: String, url: String , enableOption : Boolean)
}