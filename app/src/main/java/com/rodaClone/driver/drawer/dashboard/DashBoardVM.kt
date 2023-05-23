package com.rodaClone.driver.drawer.dashboard

import android.app.Application
import com.google.gson.Gson
import com.rodaClone.driver.base.BaseVM
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.connection.ConnectionHelper
import com.rodaClone.driver.connection.CustomException
import com.rodaClone.driver.connection.responseModels.DashboardModel
import com.rodaClone.driver.ut.SessionMaintainence
import retrofit2.Call
import javax.inject.Inject

class DashBoardVM @Inject constructor(
    val application: Application,
    val session: SessionMaintainence,
    val mConnect: ConnectionHelper
) :
    BaseVM<BaseResponse, DashBoardNavigator>(session, mConnect) {
    companion object{
        const val TAG = "DashBoardVM"
    }
    override fun onSuccessfulApi(response: BaseResponse?) {
        isLoading.value = false
        if(response?.data!=null){
            dashModel = Gson().fromJson(Gson().toJson(response.data), DashboardModel::class.java)
            getNavigator().setup()
        }
    }

    override fun onFailureApi(call: Call<BaseResponse?>?, t: CustomException?) {
        isLoading.value = false
        getNavigator().showMessage(t!!.exception!!)
    }



}
