package com.rodaClone.driver.drawer.complaints

import android.app.Application
import androidx.databinding.ObservableField
import com.google.gson.Gson
import com.rodaClone.driver.base.BaseVM
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.connection.ConnectionHelper
import com.rodaClone.driver.connection.CustomException
import com.rodaClone.driver.connection.responseModels.Complaint
import com.rodaClone.driver.ut.Config
import com.rodaClone.driver.ut.SessionMaintainence
import retrofit2.Call
import java.util.HashMap
import javax.inject.Inject

class ComplaintsVM@Inject constructor(
    val application: Application,
    val session: SessionMaintainence,
    val mConnect: ConnectionHelper
) :
    BaseVM<BaseResponse, ComplaintsNavigator>(session, mConnect) {

    var text_cmts = ObservableField("")
    /*
    apiCode 0-> getComplaints
            1-> post complaint
            2 -> get complaints of trip
     */
    var apiCode = -1
    var complaintList: List<Complaint> =ArrayList<Complaint>()
    var selectedSlug = ""
    var map = HashMap<String, String>()
    var mode = 0
    var reqId = ""

    override fun onSuccessfulApi(response: BaseResponse?) {
        isLoading.value = false
        if(apiCode==0 || apiCode == 2 && response!!.data !=null){
            val json = Gson().toJson(response?.data)
            val obj =  Gson().fromJson(json, BaseResponse.DataObjectsAllApi::class.java)
            if(obj.complaint != null){
                complaintList = obj.complaint
                getNavigator().addItems(complaintList)
            }
        }else if(apiCode==1 && response!!.success==true){
            text_cmts.set("")
            getNavigator().showSnackBar(translationModel?.txt_complaint_added_success ?: "")
            getNavigator().hideKeyFomNav()
        }
    }

    override fun onFailureApi(call: Call<BaseResponse?>?, t: CustomException?) {
        isLoading.value = false
        getNavigator().showMessage(t!!.exception!!)
    }


    fun reportComplaint(){
        if(isLoading.value == false){
            if(selectedSlug.isNotEmpty() && text_cmts.get()!!.isNotEmpty())
            {
                if(getNavigator().isNetworkConnected()){
                    apiCode = 1
                    map.clear()
                    map[Config.complaint_id] = selectedSlug
                    map[Config.answer] = text_cmts.get()!!
                    if(mode==1)
                        map[Config.request_id] = reqId
                    postComplaintBase(map)
                }
            }
            else if (text_cmts.get().isNullOrEmpty()){
                getNavigator().getBinds().complaintsET.requestFocus()
                translationModel?.let { getNavigator().showMessage(it.noComplaintsError) }
            }
        }
    }

    fun getComplaintsList(){
        if(getNavigator().isNetworkConnected()){
            apiCode=0
            getComplaintsListBase()
        }else
            getNavigator().showNetworkUnAvailable()
    }

    fun getComplaintsListOfTrip(){
        if(getNavigator().isNetworkConnected()){
            apiCode = 2
            getTripComplaintsListBase()
        }else{
            getNavigator().showNetworkUnAvailable()
        }
    }
}
