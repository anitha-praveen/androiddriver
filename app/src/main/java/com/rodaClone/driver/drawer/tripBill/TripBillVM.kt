package com.rodaClone.driver.drawer.tripBill

import android.app.Application
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.google.gson.Gson
import com.rodaClone.driver.base.BaseVM
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.connection.ConnectionHelper
import com.rodaClone.driver.connection.CustomException
import com.rodaClone.driver.connection.responseModels.RequestResponseData
import com.rodaClone.driver.ut.Config
import com.rodaClone.driver.ut.SessionMaintainence
import retrofit2.Call
import javax.inject.Inject

class TripBillVM @Inject constructor(
    val application: Application,
    val session: SessionMaintainence,
    val mConnect: ConnectionHelper
) :
    BaseVM<BaseResponse, TripBillNavigator>(session, mConnect) {
    var currency = ObservableField("")
    var mode = ObservableField(-1)
    var totaltAmount = ObservableField("")
    var map: HashMap<String, String> = HashMap()
    var requestData: RequestResponseData? = null
    var apiCall = 0
    var isContinueEnabled = ObservableBoolean()
    var cashDescription = ObservableField("")
    var tripAmount = ObservableInt(0)

    override fun onSuccessfulApi(response: BaseResponse?) {
        isLoading.value = false
    }

    override fun onFailureApi(call: Call<BaseResponse?>?, t: CustomException?) {
        isLoading.value = false
        getNavigator().showMessage(t!!.exception!!)
    }

    fun onClickConfirm(view: View) {
        getNavigator().onclickConfirm()
        // getNavigator().complaints()
    }



}