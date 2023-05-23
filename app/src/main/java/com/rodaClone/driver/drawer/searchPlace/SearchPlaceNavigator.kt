package com.rodaClone.driver.drawer.searchPlace

import android.content.Context
import com.rodaClone.driver.base.BaseViewOperator
import com.rodaClone.driver.connection.FavPlace

interface SearchPlaceNavigator : BaseViewOperator {
    fun itemSelected(favPlace: FavPlace.Favourite?)
    fun addList(favPlace: List<FavPlace.Favourite>?)
    fun getCtx():Context
    fun backPressed()
    fun goToInstantTrip()
    fun showOutStationDialog()
}