package com.rodaClone.driver.drawer.wallet

import android.app.Application
import androidx.databinding.ObservableField
import com.google.gson.Gson
import com.rodaClone.driver.base.BaseVM
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.connection.ConnectionHelper
import com.rodaClone.driver.connection.CustomException
import com.rodaClone.driver.connection.responseModels.CustomWalletModel
import com.rodaClone.driver.connection.responseModels.WalletResponsModel
import com.rodaClone.driver.ut.Config
import com.rodaClone.driver.ut.SessionMaintainence
import retrofit2.Call
import javax.inject.Inject

class WalletVm @Inject constructor(
    val application: Application,
    val session: SessionMaintainence,
    val mConnect: ConnectionHelper
) :
    BaseVM<BaseResponse, WalletNavigator>(session, mConnect) {
    val list = ArrayList<WalletResponsModel>()
    val customList = ArrayList<CustomWalletModel>()
    var singleItem = CustomWalletModel()
    var apiCall = -1
    var currencyType = ObservableField("")
    var totalAmount = ObservableField("0")
    var amountToBeAdded = ObservableField("")
    var map = HashMap<String, String>()
    var previousReq : String? = ""
    override fun onSuccessfulApi(response: BaseResponse?) {
        isLoading.value = false
        if (response!!.data != null && apiCall == 0) {
            val json = Gson().toJson(response.data)
            val obj = Gson().fromJson(json, BaseResponse.DataObjectsAllApi::class.java)
            list.clear()
            customList.clear()
            list.addAll(obj.walletTransaction!!)
            for(item in list){
                when {
                    previousReq == item.requestId -> {
                        putItIntoSingleItem(item)
                        customList.add(singleItem)
                        singleItem = CustomWalletModel()
                        previousReq = item.requestId
                    }
                    singleItem.requestId == null -> {
                        putItIntoSingleItem(item)
                        previousReq = item.requestId
                    }
                    else -> {
                        customList.add(singleItem)
                        singleItem = CustomWalletModel()
                        putItIntoSingleItem(item)
                        previousReq = item.requestId
                    }
                }
            }
            getNavigator().setUpAdapter(customList, obj)
            currencyType.set(obj.currency)
            totalAmount.set(obj.currency + obj.totalAmount.toString())

        } else {
            getListApi()
        }
    }

    fun putItIntoSingleItem(item : WalletResponsModel){
        singleItem.requestId = item.requestId
        singleItem.purpose = item.purpose
        when {
            item.purpose.equals("Admin Commission",true) -> singleItem.adminCommissionAmount = item.amount
            item.purpose.equals("Service Tax",true) -> singleItem.serviceTaxAmount = item.amount
            else -> singleItem.amount = item.amount
        }
        singleItem.date = item.createdAt
    }

    override fun onFailureApi(call: Call<BaseResponse?>?, t: CustomException?) {
        isLoading.value = false
        getNavigator().showMessage(t!!.exception!!)
    }

    fun getListApi() {
        apiCall = 0
        getWalletListApiBase()
    }

    fun addAmountApi() {
        if (amountToBeAdded.get()!!.isEmpty() || amountToBeAdded.get()!!.toDouble()<=0.0) {
            getNavigator().showToast("Kindly add a valid amount")
        } else {
            apiCall = 1
            map[Config.amount] = amountToBeAdded.get()!!
            map[Config.purpose] = "purpose"
            getWalletAddAmountBase(map)

        }
    }

    fun addAmount(amount: String) {
        amountToBeAdded.set(amount)
    }


}