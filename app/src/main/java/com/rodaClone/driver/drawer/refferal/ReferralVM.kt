package com.rodaClone.driver.drawer.refferal

import android.app.Application
import androidx.databinding.ObservableField
import com.google.gson.Gson
import com.rodaClone.driver.base.BaseVM
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.connection.ConnectionHelper
import com.rodaClone.driver.connection.CustomException
import com.rodaClone.driver.ut.SessionMaintainence
import retrofit2.Call
import javax.inject.Inject

class ReferralVM @Inject constructor(
    val application: Application,
    val session: SessionMaintainence,
    val mConnect: ConnectionHelper
) :
    BaseVM<BaseResponse, ReferralNavigator>(session, mConnect) {

    var currencySymbol = ObservableField("")
    var prize = ObservableField("")
    var code = ObservableField("")

    var refContent = ObservableField("")
    var refAmount = ObservableField("")

    override fun onSuccessfulApi(response: BaseResponse?) {
        isLoading.value = false

        val json = Gson().toJson(response!!.data)
        val obj = Gson().fromJson(json, BaseResponse.DataObjectsAllApi::class.java)
        obj.refAmount?.let { amt ->
            obj.currency_symbol?.let { s ->
                refAmount.set("$amt $s")
                obj.referByDriverAmount?.let { ua ->
                    refContent.set("${translationModel?.txt_invite_content} $ua $s")
                }
            }
        }
        code.set(obj.refCode)
    }

    override fun onFailureApi(call: Call<BaseResponse?>?, t: CustomException?) {
        isLoading.value = false
        getNavigator().showMessage(t!!.exception!!)
    }

    fun getReferalApi() {
        if (getNavigator().isNetworkConnected()) {
            getDriverReferal()
        } else getNavigator().showNetworkUnAvailable()
    }

    fun onClickShare(){
        if (isLoading.value == false)
            getNavigator().onClickShare()

    }
}