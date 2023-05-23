package com.rodaClone.driver.drawer.history.completedHistory

import com.rodaClone.driver.base.BaseViewOperator
import com.rodaClone.driver.connection.responseModels.HistoryModel
import com.rodaClone.driver.connection.responseModels.RequestResponseData

interface CompletedHistoryNavigator : BaseViewOperator {
    fun loadData(data: MutableList<HistoryModel.History>?)
    fun invoice(data : RequestResponseData)
    fun startSimmer()
    fun stopSimmer()
    fun stopLoader()
    fun mentionLastPage()
    fun goToComplaints(reqId:String)
}