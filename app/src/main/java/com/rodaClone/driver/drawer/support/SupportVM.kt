package com.rodaClone.driver.drawer.support

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.view.View
import com.rodaClone.driver.base.BaseVM
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.connection.ConnectionHelper
import com.rodaClone.driver.connection.CustomException
import com.rodaClone.driver.ut.SessionMaintainence
import retrofit2.Call
import javax.inject.Inject

class SupportVM @Inject constructor(
    val application: Application,
    val session: SessionMaintainence,
    val mConnect: ConnectionHelper
) :
    BaseVM<BaseResponse, SupportNavigator>(session, mConnect) {


    override fun onSuccessfulApi(response: BaseResponse?) {
        isLoading.value = false
    }

    override fun onFailureApi(call: Call<BaseResponse?>?, t: CustomException?) {
        isLoading.value = false
        getNavigator().showMessage(t!!.exception!!)
    }

    fun openComplaints(view : View){
        getNavigator().complaints()
    }
    fun openSOS(view : View){
        getNavigator().sos()
    }
    fun openFAQ(view : View){
        getNavigator().faq()
    }
    fun onclickCall(view : View){
        if(!translationModel?.txt_admin_number.isNullOrEmpty()){
            val callIntent = Intent(Intent.ACTION_DIAL)
            callIntent.data = Uri.parse("tel:${translationModel?.txt_admin_number}")
            view.context.startActivity(callIntent)
        }
    }
}