package com.rodaClone.driver.drawer

import android.app.Dialog
import android.content.Context
import android.content.Intent
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.rodaClone.driver.base.BaseViewOperator
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.connection.responseModels.RequestResponseData

interface DrawerNavigator : BaseViewOperator {
    fun openSideMenu()
    fun setImage(url: String)
    fun openAcceptRejectAct(data: RequestResponseData?)
    fun openTripData(data: RequestResponseData?, driver: BaseResponse.ReqDriverData)
    fun setFloatService(b: Boolean)
    fun openLoginPage()
    fun openMapFragment()
    fun openInvoice(data : RequestResponseData)
    fun showTripCancelled()
    fun updateTripAddress(intent : Intent)
    fun openApproveDeclinePage(approveStatus: Int?, documentStatus: Int?, blockReason : String? , showSubscribe : Boolean)
    fun callLogoutFromActivity()
    fun getCtx() : Context
    fun getCurrentDest() : String
    fun checkForAdditionalPermissions()
    fun checkUpdate()
    fun getOptimizingDialog() : Dialog?
    fun getMiPermission() : Dialog?
    fun getmAlert() : BottomSheetDialog?
    fun currentLoc()
}