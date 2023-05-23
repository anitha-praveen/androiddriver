package com.rodaClone.driver.drawer.dashboard.dashRateReward

import android.app.Application
import androidx.databinding.ObservableField
import com.rodaClone.driver.base.BaseVM
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.connection.ConnectionHelper
import com.rodaClone.driver.connection.CustomException
import com.rodaClone.driver.ut.SessionMaintainence
import retrofit2.Call
import javax.inject.Inject

class DashRateRewardVM @Inject constructor(
    val application: Application,
    val session: SessionMaintainence,
    val mConnect: ConnectionHelper
) :
    BaseVM<BaseResponse, DashRateRewardNavigator>(session, mConnect) {

    var acceptanceRatio = ObservableField("")
    var review = ObservableField("")
    var rewardPoint = ObservableField("")
    override fun onSuccessfulApi(response: BaseResponse?) {
        isLoading.value = false

    }

    override fun onFailureApi(call: Call<BaseResponse?>?, t: CustomException?) {
        isLoading.value = false
        getNavigator().showMessage(t!!.exception!!)
    }



}
