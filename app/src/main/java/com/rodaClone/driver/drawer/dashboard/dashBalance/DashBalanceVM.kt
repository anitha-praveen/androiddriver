package com.rodaClone.driver.drawer.dashboard.dashBalance

import android.app.Application
import androidx.databinding.ObservableField
import com.rodaClone.driver.base.BaseVM
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.connection.ConnectionHelper
import com.rodaClone.driver.connection.CustomException
import com.rodaClone.driver.ut.SessionMaintainence
import retrofit2.Call
import javax.inject.Inject

class DashBalanceVM @Inject constructor(
    val application: Application,
    val session: SessionMaintainence,
    val mConnect: ConnectionHelper
) :
    BaseVM<BaseResponse, DashBalanceNavigator>(session, mConnect) {
    var todayBal = ObservableField("")
    var yesterdayBal = ObservableField("")
    var weekBal = ObservableField("")
    var monthBal = ObservableField("")
    var totalAMnt = ObservableField("")
    override fun onSuccessfulApi(response: BaseResponse?) {
        isLoading.value = false

    }

    override fun onFailureApi(call: Call<BaseResponse?>?, t: CustomException?) {
        isLoading.value = false
        getNavigator().showMessage(t!!.exception!!)
    }



}
