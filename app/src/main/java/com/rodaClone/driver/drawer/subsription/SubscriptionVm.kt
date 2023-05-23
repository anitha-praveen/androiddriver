package com.rodaClone.driver.drawer.subsription

import android.app.Application
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.google.gson.Gson
import com.rodaClone.driver.base.BaseVM
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.connection.ConnectionHelper
import com.rodaClone.driver.connection.CustomException
import com.rodaClone.driver.connection.SubscriptionModel
import com.rodaClone.driver.ut.SessionMaintainence
import retrofit2.Call
import java.util.ArrayList
import javax.inject.Inject

class SubscriptionVm @Inject constructor(
    val application: Application,
    val session: SessionMaintainence,
    val mConnect: ConnectionHelper
) :
    BaseVM<BaseResponse, SubscriptionNavigator>(session, mConnect) {
    private val list = ArrayList<SubscriptionModel>()
    var userName = ObservableField("")
    var currencySymbol = ObservableField("")
    var apiCall: Int = 0
    val map: HashMap<String, String> = HashMap()
    var allowSubscription = ObservableBoolean(false)
    val isenglish = ObservableBoolean(true)
    override fun onSuccessfulApi(response: BaseResponse?) {
        isLoading.value = false
        if (apiCall == 1) {
            if (response!!.data != null) {
                val json = Gson().toJson(response.data)
                val obj = Gson().fromJson(json, BaseResponse.DataObjectsAllApi::class.java)
                /* allowSubscription needed to be initialized before setting up the adapter */
                obj.subscriptionMode?.let {
                    allowSubscription.set(
                        it.equals(
                            "SUBSCRIPTION",
                            true
                        ) || it.equals("BOTH", true)
                    )
                }
                currencySymbol.set(obj.currency_symbol)
                list.clear()
                list.addAll(obj.subscription)
                getNavigator().setAdapter(list)
                if (obj.profile_pic != null) {
                    getNavigator().setImage(obj.profile_pic!!)
                }
                userName.set(obj.user_name)
//                Log.e(SubscriptionFragment.TAG,"${allowSubscription.get()}")
            }
        } else {
            getNavigator().showMessage("Subscribed Success")
        }


    }

    override fun onFailureApi(call: Call<BaseResponse?>?, t: CustomException?) {
        isLoading.value = false
        getNavigator().showMessage(t!!.exception!!)
    }

    fun getApi() {
        if (getNavigator().isNetworkConnected()) {
            apiCall = 1
            getSubscriptionListBase()
        } else getNavigator().showNetworkUnAvailable()

    }

    fun addSubscription(id: String?) {
        if (getNavigator().isNetworkConnected()) {
            apiCall = 2
            map.clear()
            map["subscription_id"] = id!!
            subscriptionAdd(map)
        } else getNavigator().showNetworkUnAvailable()

    }


}