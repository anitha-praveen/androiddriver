package com.rodaClone.driver.dialogs.cancel

import android.content.Context
import com.rodaClone.driver.base.BaseViewOperator
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.connection.responseModels.Country

interface CancelListNavigator : BaseViewOperator {
    fun clickedItem(country: Country)
    fun dismissDialog()
    fun loadReasons(reasons: List<BaseResponse.CancelReason>?)
    fun getSelectedPosition(): Int
    fun closeTripFragment()
    fun getCtx():Context
}