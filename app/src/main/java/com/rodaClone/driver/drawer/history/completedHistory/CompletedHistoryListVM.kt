package com.rodaClone.driver.drawer.history.completedHistory

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

class CompletedHistoryListVM @Inject constructor(
    val session: SessionMaintainence,
    val mConnect: ConnectionHelper

) :
    BaseVM<BaseResponse, CompletedHistoryNavigator>(session, mConnect) {
    var hashMap = HashMap<String, String>()
    var previousPage = 1

    var currentPage: Int = 1
    var totalPages = 1000
    var isLastPage = false
    var noItemFound = ObservableBoolean(true)
    var theList = ArrayList<BaseResponse.DataObjectsAllApi>()

    override fun onSuccessfulApi(response: BaseResponse?) {
        isLoading.value = false
        getNavigator().stopSimmer()
        if (response != null) {
            val historyModel: HistoryModel =
                Gson().fromJson(Gson().toJson(response.data), HistoryModel::class.java)
            if (historyModel.data!!.size > 0) {
                noItemFound.set(false)
                if (currentPage > previousPage ) {
                    getNavigator().stopLoader()
                }
                historyModel.lastPage?.let {
                    totalPages  = it
                }
                getNavigator().loadData(historyModel.data)
            } else {
                historyModel.lastPage?.let {
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
            hashMap[Config.trip_type] = "COMPLETED"
            getHistoryBase(hashMap, "$currentPage")
        }
    }

}