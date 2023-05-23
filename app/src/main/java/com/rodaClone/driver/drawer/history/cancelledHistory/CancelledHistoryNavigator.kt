package com.rodaClone.driver.drawer.history.cancelledHistory

import com.rodaClone.driver.base.BaseViewOperator
import com.rodaClone.driver.connection.responseModels.HistoryModel
import com.rodaClone.driver.connection.responseModels.RequestResponseData


interface CancelledHistoryNavigator : BaseViewOperator {
        fun loadData(data: MutableList<HistoryModel.History>)
        fun invoice(data : RequestResponseData)
        fun startSimmer()
        fun stopSimmer()
        fun showText()
        fun hideText()
        fun stopLoader()
        fun mentionLastPage()
        fun goToComplaints(reqId:String)
}