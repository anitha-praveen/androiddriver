package com.rodaClone.driver.drawer.sos

import com.rodaClone.driver.base.BaseViewOperator
import com.rodaClone.driver.connection.responseModels.Sos

interface SosNavigator : BaseViewOperator {
    fun onPhoneClick(number : String)
    fun addList(sos : List<Sos>?)
    fun showSosAdd()
    fun deleteSosNav(slug : String)
}