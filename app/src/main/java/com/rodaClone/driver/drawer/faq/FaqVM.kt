package com.rodaClone.driver.drawer.faq

import android.app.Application
import androidx.databinding.ObservableBoolean
import com.google.gson.Gson
import com.rodaClone.driver.base.BaseVM
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.connection.ConnectionHelper
import com.rodaClone.driver.connection.CustomException
import com.rodaClone.driver.connection.responseModels.Faq
import com.rodaClone.driver.ut.SessionMaintainence
import retrofit2.Call
import java.util.ArrayList
import javax.inject.Inject

class FaqVM @Inject constructor(
    val application: Application,
    val session: SessionMaintainence,
    val mConnect: ConnectionHelper
) :
    BaseVM<BaseResponse, FaqNavigator>(session, mConnect) {

    var isFaqAvailable = ObservableBoolean(false)
    var list = ArrayList<Faq>()

    override fun onSuccessfulApi(response: BaseResponse?) {
        isLoading.value = false
        if (response!!.data!= null){
            val json = Gson().toJson(response.data)
            val obj = Gson().fromJson(json, BaseResponse::class.java)
            list.addAll(obj.faq!!)
            getNavigator().setFaqAdapter(list)
        }
//        if(response!!.data!=null){
//            val mapFAQ: HashMap<String, MutableList<Faq>> =
//                HashMap<String, MutableList<Faq>>()
//            val titleFAQ: MutableList<String> = ArrayList()
//            val json = Gson().toJson(response.data)
//            val obj =  Gson().fromJson(json, BaseResponse.DataObjectsAllApi::class.java)
//            for (iterationFaq in obj.faq!!) {
//                if (mapFAQ.containsKey(iterationFaq.category)) {
//                    var tempList: MutableList<Faq>? = mapFAQ[iterationFaq.category]
//                    if (tempList != null) tempList.add(iterationFaq) else {
//                        tempList = ArrayList<Faq>()
//                        tempList.add(iterationFaq)
//                    }
//                    mapFAQ[iterationFaq.category!!] = tempList
//                } else {
//                    val tempList: MutableList<Faq> = ArrayList<Faq>()
//                    tempList.add(iterationFaq)
//                    titleFAQ.add(iterationFaq.category!!)
//                    mapFAQ[iterationFaq.category] = tempList
//                }
//            }
//            if (mapFAQ.size > 0) {
//                getNavigator().setFaqAdapter(titleFAQ, mapFAQ)
//                isFaqAvailable.set(true)
//            } else isFaqAvailable.set(false)
//        }

//        if(response!!.data!=null){
//            val mapFAQ: HashMap<String, MutableList<Faq>> =
//                HashMap<String, MutableList<Faq>>()
//            val titleFAQ: MutableList<String> = ArrayList()
//            val json = Gson().toJson(response.data)
//            val obj =  Gson().fromJson(json, BaseResponse.DataObjectsAllApi::class.java)
//            for (iterationFaq in obj.faq!!) {
//                if (mapFAQ.containsKey(iterationFaq.category)) {
//                    var tempList: MutableList<Faq>? = mapFAQ[iterationFaq.category]
//                    if (tempList != null) tempList.add(iterationFaq) else {
//                        tempList = ArrayList<Faq>()
//                        tempList.add(iterationFaq)
//                    }
//                   // mapFAQ[iterationFaq.category!!] = tempList
//                    mapFAQ
//                } else {
//                    val tempList: MutableList<Faq> = ArrayList<Faq>()
//                    tempList.add(iterationFaq)
//                    titleFAQ.add(iterationFaq.category!!)
//                    mapFAQ[iterationFaq.category] = tempList
//                }
//            }
//            if (mapFAQ.size > 0) {
//                getNavigator().setFaqAdapter(titleFAQ, mapFAQ)
//                isFaqAvailable.set(true)
//            } else isFaqAvailable.set(false)
//        }


    }

    override fun onFailureApi(call: Call<BaseResponse?>?, t: CustomException?) {
        isLoading.value = false
        getNavigator().showMessage(t!!.exception!!)
    }



}
