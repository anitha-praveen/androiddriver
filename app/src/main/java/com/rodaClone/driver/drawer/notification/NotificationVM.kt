package com.rodaClone.driver.drawer.notification

import android.app.Application
import androidx.databinding.ObservableBoolean
import com.google.gson.Gson
import com.rodaClone.driver.base.BaseVM
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.connection.ConnectionHelper
import com.rodaClone.driver.connection.CustomException
import com.rodaClone.driver.connection.responseModels.NotificationData
import com.rodaClone.driver.connection.responseModels.NotificationResponseModel
import com.rodaClone.driver.ut.SessionMaintainence
import retrofit2.Call
import javax.inject.Inject

class NotificationVM @Inject constructor(
    val application: Application,
    val session: SessionMaintainence,
    val mConnect: ConnectionHelper
) :
    BaseVM<BaseResponse, NotificationNavigator>(session, mConnect) {
    val notificationList = ArrayList<NotificationData>()
    var isLastPage = false
    var totalPages = 1000
    var currentPage = 1
    var previousPage = 1
    val showShimmer = ObservableBoolean(false)
    val showNoNotification = ObservableBoolean(false)
    override fun onSuccessfulApi(response: BaseResponse?) {
        isLoading.value = false
        if (response != null){
            val notificationResponse: NotificationResponseModel =
                Gson().fromJson(Gson().toJson(response.data), NotificationResponseModel::class.java)
            if (notificationResponse.data!!.size>0){
                if (currentPage > previousPage) {
                    getNavigator().stopLoader()
                }
                totalPages = notificationResponse.lastPage!!
                notificationList.clear()
                notificationList.addAll(notificationResponse.data!!)
                getNavigator().stopSimmer()
                getNavigator().addList(notificationList)
            }
            else{
                if (currentPage >= notificationResponse.lastPage!! ){
                    getNavigator().stopSimmer()
                    getNavigator().mentionLastPage()
                }
            }
            if (notificationList.size == 0)
                showNoNotification.set(true)
            else
                showNoNotification.set(false)

        }

    }

    override fun onFailureApi(call: Call<BaseResponse?>?, t: CustomException?) {
        isLoading.value = false
        getNavigator().showMessage(t!!.exception!!)
    }
    fun getNotifications(){
        getNotifactionBase(currentPage.toString())
    }
}