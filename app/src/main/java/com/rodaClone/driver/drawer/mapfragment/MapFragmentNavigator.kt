package com.rodaClone.driver.drawer.mapfragment

import android.content.Context
import android.location.Location
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.model.BitmapDescriptor
import com.rodaClone.driver.base.BaseViewOperator

interface MapFragmentNavigator : BaseViewOperator {
    fun openSos()
    fun openInstantTrip()
    fun getAct(): FragmentActivity?
    fun startOnetimeReq(location: Location)
    fun getCtx():Context?
    fun getMarkerIcon(icPickPin: Int): BitmapDescriptor?

}