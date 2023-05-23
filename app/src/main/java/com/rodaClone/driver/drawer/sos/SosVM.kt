package com.rodaClone.driver.drawer.sos

import android.app.Application
import com.google.gson.Gson
import com.rodaClone.driver.base.BaseVM
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.connection.ConnectionHelper
import com.rodaClone.driver.connection.CustomException
import com.rodaClone.driver.ut.Config
import com.rodaClone.driver.ut.SessionMaintainence
import retrofit2.Call
import javax.inject.Inject

class SosVM @Inject constructor(
    val application: Application,
    val session: SessionMaintainence,
    val mConnect: ConnectionHelper
) :
    BaseVM<BaseResponse, SosNavigator>(session, mConnect) {

    var apiCode = -1

    /*
    apiCode 0 -> get Sos List
            1 -> save sos
            2 -> delete sos
     */
    var hashMap = HashMap<String, String>()

    override fun onSuccessfulApi(response: BaseResponse?) {
        isLoading.value = false
        if (apiCode == 0 && response!!.data != null) {
            val sos: BaseResponse.DataObjectsAllApi =
                Gson().fromJson(
                    Gson().toJson(response.data),
                    BaseResponse.DataObjectsAllApi::class.java
                )
            getNavigator().addList(sos.sos)
        } else if (apiCode == 1 || apiCode==2)
            getSosList()

    }

    override fun onFailureApi(call: Call<BaseResponse?>?, t: CustomException?) {
        isLoading.value = false
        getNavigator().showMessage(t!!.exception!!)
    }

    fun getSosList() {
        if (getNavigator().isNetworkConnected()) {
            apiCode = 0
            getSOSListBase()
        } else
            getNavigator().showNetworkUnAvailable()
    }

    fun saveSos(name: String, phone: String) {
        if (getNavigator().isNetworkConnected()) {
            apiCode = 1
            hashMap.clear()
            hashMap[Config.title] = name
            hashMap[Config.phone_number] = phone
            saveSosBase(hashMap)
        }
    }

    fun deleteSos(slug : String){
        if(getNavigator().isNetworkConnected()){
            apiCode = 2
            deleteSosBase(slug)
        }
    }

    fun onClickAdd() {
        if (!isLoading.value!!)
            getNavigator().showSosAdd()
    }


}
