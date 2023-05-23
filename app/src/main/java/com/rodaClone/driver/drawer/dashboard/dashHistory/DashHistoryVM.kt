package com.rodaClone.driver.drawer.dashboard.dashHistory

import android.app.Application
import androidx.databinding.ObservableField
import com.rodaClone.driver.base.BaseVM
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.connection.ConnectionHelper
import com.rodaClone.driver.connection.CustomException
import com.rodaClone.driver.ut.SessionMaintainence
import retrofit2.Call
import javax.inject.Inject

class DashHistoryVM @Inject constructor(
    val application: Application,
    val session: SessionMaintainence,
    val mConnect: ConnectionHelper
) :
    BaseVM<BaseResponse, DashHistoryNavigator>(session, mConnect) {
    var totalTrip = ObservableField("")
    var company_commision = ObservableField("")
    var driver_commision = ObservableField("")
    var total_cash = ObservableField("")
    var total_card = ObservableField("")
    var total_wallet = ObservableField("")
    var monthly_trips = ObservableField("")
    var weekly_trips = ObservableField("")
    var daily_trips = ObservableField("")
    override fun onSuccessfulApi(response: BaseResponse?) {
        isLoading.value = false
    }

    override fun onFailureApi(call: Call<BaseResponse?>?, t: CustomException?) {
        isLoading.value = false
        getNavigator().showMessage(t!!.exception!!)
    }



}
