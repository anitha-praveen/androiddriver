package com.rodaClone.driver.drawer.appStatus

import com.rodaClone.driver.base.BaseViewOperator

interface AppStatusNavigator:BaseViewOperator {
    fun openLocationSetting()
    fun openBatteryOptimization()
    fun callReqinPro()
    fun openAutoStart()
}