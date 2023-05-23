package com.rodaClone.driver.drawer.history.cancelledHistory

import androidx.databinding.ObservableBoolean
import com.google.gson.Gson
import com.rodaClone.driver.base.BaseVM
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.connection.ConnectionHelper
import com.rodaClone.driver.connection.CustomException
import com.rodaClone.driver.connection.responseModels.HistoryModel
import com.rodaClone.driver.ut.Config
import com.rodaClone.driver.ut.SessionMaintainence
import retrofit2.Call
import javax.inject.Inject

class CancelledHistoryVM @Inject constructor(
    val session: SessionMaintainence,
    val mConnect: ConnectionHelper

) :
    BaseVM<BaseResponse, CancelledHistoryNavigator>(session, mConnect) {
    var hashMap = HashMap<String, String>()
    var previousPage = 1

    var currentPage: Int = 1
    var totalPages = 1000
    var isLastPage = false
    var noItemFound = ObservableBoolean(true)

    override fun onSuccessfulApi(response: BaseResponse?) {
        isLoading.value = false
        getNavigator().stopSimmer()
        if (response != null) {
            val model: HistoryModel =
                Gson().fromJson(Gson().toJson(response.data), HistoryModel::class.java)
            if (model.data!!.size > 0) {
                noItemFound.set(false)
                if (currentPage > previousPage) {
                    getNavigator().stopLoader()
                }
                model.lastPage?.let {
                    totalPages = it
                }
                getNavigator().loadData(model.data)
            } else {
                model.lastPage?.let {
                    if (currentPage >= it)
                        getNavigator().mentionLastPage()
                }
            }
        }
    }

    override fun onFailureApi(call: Call<BaseResponse?>?, t: CustomException?) {
        isLoading.value = false
        getNavigator().showMessage(t!!.exception!!)
        getNavigator().stopSimmer()
    }

    fun getHistoryList() {
        if (getNavigator().isNetworkConnected()) {
            getNavigator().startSimmer()
            hashMap.clear()
            hashMap[Config.ride_type] = "RIDE NOW"
            hashMap[Config.trip_type] = "CANCELLED"
            getHistoryBase(hashMap, "$currentPage")
        }
    }

}