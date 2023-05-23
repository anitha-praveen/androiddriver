package com.rodaClone.driver.drawer.document.documentsList

import android.app.Application
import androidx.databinding.ObservableBoolean
import com.google.gson.Gson
import com.rodaClone.driver.base.BaseVM
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.connection.ConnectionHelper
import com.rodaClone.driver.connection.CustomException
import com.rodaClone.driver.ut.SessionMaintainence
import retrofit2.Call
import javax.inject.Inject

class DocumentsListVM @Inject constructor(
    val application: Application,
    val session: SessionMaintainence,
    val mConnect: ConnectionHelper
) :
    BaseVM<BaseResponse, DocumentsListNavigator>(session, mConnect) {

    private var apiCall = -1
    var isAllReqDocsUploaded = ObservableBoolean(false)

    override fun onSuccessfulApi(response: BaseResponse?) {
        isLoading.value = false
        if(response!!.data!=null){
           when(apiCall){
               1 ->{
                   val documents = Gson().fromJson(Gson().toJson(response.data),BaseResponse.DataObjectsAllApi::class.java)
                   getNavigator().addList(documents.document)
                    isAllReqDocsUploaded.set(documents.allDocumentsUpload == true)
               }
           }

        }
    }

    override fun onFailureApi(call: Call<BaseResponse?>?, t: CustomException?) {
        isLoading.value = false
        getNavigator().showMessage(t!!.exception!!)
    }


    fun getGroupDocument(){
        apiCall = 1
        if(getNavigator().isNetworkConnected())
            getGroupDocumentBase()
        else
            getNavigator().showNetworkUnAvailable()
    }

    fun onClickSubmit(){
        getNavigator().callReqProgDrawerAct()
    }
}