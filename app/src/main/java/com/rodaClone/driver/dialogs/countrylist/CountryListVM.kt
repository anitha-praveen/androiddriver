package com.rodaClone.driver.dialogs.countrylist

import android.view.View
import androidx.databinding.ObservableBoolean
import com.rodaClone.driver.base.BaseVM
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.connection.ConnectionHelper
import com.rodaClone.driver.connection.CustomException
import com.rodaClone.driver.ut.SessionMaintainence
import retrofit2.Call
import javax.inject.Inject

class CountryListVM @Inject constructor(
    val session: SessionMaintainence,
    val mConnect: ConnectionHelper

) : BaseVM<BaseResponse, CountryListNavigator>(session, mConnect) {
    var showNoItem = ObservableBoolean(false)

    var noItem: ObservableBoolean = ObservableBoolean(false)

    override fun onSuccessfulApi(response: BaseResponse?) {
        isLoading.value = false
    }

    override fun onFailureApi(call: Call<BaseResponse?>?, t: CustomException?) {
        isLoading.value = false
        getNavigator().showMessage(t!!.exception!!)
    }

    fun onClickBack(view: View) { getNavigator().close() }
}