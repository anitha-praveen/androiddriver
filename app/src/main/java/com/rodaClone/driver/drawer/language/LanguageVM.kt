package com.rodaClone.driver.drawer.language

import androidx.databinding.ObservableField
import com.google.gson.Gson
import com.rodaClone.driver.base.BaseVM
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.connection.ConnectionHelper
import com.rodaClone.driver.connection.CustomException
import com.rodaClone.driver.connection.responseModels.AvailableCountryAndKLang
import com.rodaClone.driver.ut.Config
import com.rodaClone.driver.ut.SessionMaintainence
import retrofit2.Call
import javax.inject.Inject

class LanguageVM @Inject constructor(
    val session: SessionMaintainence,
    val mConnect: ConnectionHelper
) :
    BaseVM<BaseResponse, LanguageNavigator>(session, mConnect) {

    var apiCode = -1
    /* apiCode 1 -> update backend about language change
               2 -> get translation for selected language
     */

    override fun onSuccessfulApi(response: BaseResponse?) {
        isLoading.value = false
        if(apiCode==1){
            getTrans()
        }else if(apiCode == 2){
            if (response!!.data != null) {
                session.saveString(SessionMaintainence.TRANSLATED_DATA, Gson().toJson(response.data!!))
                getNavigator().refresh()
            }
        }
    }


    fun onClickSetLanguage() {
        if(selectedLangCode.get()!!.isNotEmpty()){
            if(getNavigator().isNetworkConnected()){
                apiCode = 1
                val map = HashMap<String, String>()
                map[Config.language] = selectedLangCode.get()!!
                getPostSelectedLanguage(map)
            }else
                getNavigator().showNetworkUnAvailable()
        }else{
            getNavigator().close()
        }
    }

    private fun getTrans(){
        if(getNavigator().isNetworkConnected()){
            apiCode = 2
            getSelectedLangBase(selectedLangCode.get()!!)
        }
        else
            getNavigator().showNetworkUnAvailable()
    }

    lateinit var data: AvailableCountryAndKLang
    var selectedLangCode = ObservableField("")
    override fun onFailureApi(call: Call<BaseResponse?>?, t: CustomException?) {
        isLoading.value = false
        getNavigator().showMessage(t!!.exception!!)
    }

}
