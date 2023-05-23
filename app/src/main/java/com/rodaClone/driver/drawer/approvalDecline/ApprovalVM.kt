package com.rodaClone.driver.drawer.approvalDecline

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

class ApprovalVM @Inject constructor(
    val application: Application,
    val session: SessionMaintainence,
    val mConnect: ConnectionHelper
) :
    BaseVM<BaseResponse, ApprovalNavigator>(session, mConnect) {

    var buttonText: ObservableField<String> = ObservableField("")
    var reason = ObservableField("")
    var showSubscribe = ObservableBoolean(false)
    val title_reson = ObservableField("")

    override fun onSuccessfulApi(response: BaseResponse?) {
        isLoading.value = false

    }

    override fun onFailureApi(call: Call<BaseResponse?>?, t: CustomException?) {
        isLoading.value = false
        getNavigator().showMessage(t!!.exception!!)
    }

    fun onClickButton() {
        getNavigator().handleClick()
    }

    fun onClickSubscribe(){
        getNavigator().openSubscription()
    }

    fun onMenuClick(){
        getNavigator().openSideMenu()
    }
}
