package com.rodaClone.driver.loginSignup.getStarteedScrn

import androidx.databinding.ObservableField
import com.google.gson.Gson
import com.rodaClone.driver.base.BaseVM
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.connection.ConnectionHelper
import com.rodaClone.driver.connection.CustomException
import com.rodaClone.driver.ut.SessionMaintainence
import retrofit2.Call
import javax.inject.Inject

class GetStartedScreenVM @Inject constructor(
    val session: SessionMaintainence,
    val mConnect: ConnectionHelper

) :
    BaseVM<BaseResponse, GetStartedScreenNavigator>(session, mConnect) {
    var selectedLangCode = ObservableField("")
    val gson = Gson()
    override fun onSuccessfulApi(response: BaseResponse?) {
        isLoading.value = false
        if (response!!.data != null) {
            session.saveString(SessionMaintainence.TRANSLATED_DATA, gson.toJson(response.data!!))

            getNavigator().initiateNaviagation()
        }
    }

    override fun onFailureApi(call: Call<BaseResponse?>?, t: CustomException?) {
        isLoading.value = false
        getNavigator().showMessage(t!!.exception!!)
    }

    /*  Calling selected language api */
    fun onClickSetLanguage() {
        if (selectedLangCode.get()!!.isNotEmpty())
            getSelectedLangBase(selectedLangCode.get()!!)
        else
            getNavigator().showMessage("Set Language")
    }


}