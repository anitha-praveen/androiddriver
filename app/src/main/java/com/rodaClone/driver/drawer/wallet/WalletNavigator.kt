package com.rodaClone.driver.drawer.wallet

import com.rodaClone.driver.base.BaseViewOperator
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.connection.responseModels.CustomWalletModel

interface WalletNavigator : BaseViewOperator {
    fun showToast(message:String)
    fun setUpAdapter(list:ArrayList<CustomWalletModel>,type:BaseResponse.DataObjectsAllApi)
    fun openInvoice(reqId:String)
}