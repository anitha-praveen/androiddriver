package com.rodaClone.driver.drawer.mapfragment.instantTrip

import android.content.Context
import com.rodaClone.driver.base.BaseViewOperator
import com.rodaClone.driver.connection.BaseResponse

interface InstantTripNavigator :BaseViewOperator {
    fun getCtx():Context
    fun openSearchPlace()
    fun pickAddressPressed()
    fun currentLocation()
    fun showDialog()
    fun openTrip(data:BaseResponse.DataObjectsAllApi)
}