package com.rodaClone.driver.drawer.notification

import com.rodaClone.driver.base.BaseViewOperator
import com.rodaClone.driver.connection.responseModels.NotificationData

interface NotificationNavigator : BaseViewOperator{
    fun addList(list:ArrayList<NotificationData>)
    fun stopLoader()
    fun mentionLastPage()
    fun startSimmer()
    fun stopSimmer()
}