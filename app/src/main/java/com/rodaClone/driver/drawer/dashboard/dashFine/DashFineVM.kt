package com.rodaClone.driver.drawer.dashboard.dashFine

import android.app.Application
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.rodaClone.driver.base.BaseVM
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.connection.ConnectionHelper
import com.rodaClone.driver.connection.CustomException
import com.rodaClone.driver.ut.SessionMaintainence
import retrofit2.Call
import javax.inject.Inject

class DashFineVM @Inject constructor(
    val application: Application,
    val session: SessionMaintainence,
    val mConnect: ConnectionHelper
) :
    BaseVM<BaseResponse, DashFineNavigator>(session, mConnect) {
    var fineVisible = ObservableBoolean(false)
    var fineAmnt = ObservableField("")
    var fineReason = ObservableField("")
    var captainAmnt = ObservableField("")
    var customerAmnt = ObservableField("")
    var netAmnt = ObservableField("")
    var bonusAmnt = ObservableField("")
    var bonusDesc = ObservableField("")
    var netProfitAmnt = ObservableField("")
    var bonusVisible = ObservableBoolean(false)

    override fun onSuccessfulApi(response: BaseResponse?) {
        isLoading.value = false

    }

    override fun onFailureApi(call: Call<BaseResponse?>?, t: CustomException?) {
        isLoading.value = false
        getNavigator().showMessage(t!!.exception!!)
    }



}
