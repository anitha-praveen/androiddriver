package com.rodaClone.driver.loginSignup.signup

import android.view.View
import androidx.databinding.ObservableField
import com.google.gson.Gson
import com.rodaClone.driver.base.BaseVM
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.connection.ConnectionHelper
import com.rodaClone.driver.ut.SessionMaintainence
import retrofit2.Call
import javax.inject.Inject
import com.rodaClone.driver.connection.CustomException
import com.rodaClone.driver.loginSignup.vehicleFrag.model.SignupServiceLocData
import com.rodaClone.driver.ut.Utilz
import java.util.regex.Pattern

import androidx.databinding.ObservableBoolean
import com.rodaClone.driver.connection.responseModels.CompanyModel
import kotlin.collections.ArrayList


class SignupFragVM @Inject constructor(
    val session: SessionMaintainence,
    val mConnect: ConnectionHelper

) :
    BaseVM<BaseResponse, SignupFragNavigator>(session, mConnect) {
    var countrycode = ObservableField("")
    var countrycodeID = ObservableField("")
    var fname = ObservableField("")
    var lname = ObservableField("")
    var email = ObservableField("")
    var phonenumber = ObservableField("")
    var serviceLocationSlug = ObservableField("")
    var referralCode = ObservableField("")
    var msg: String = ""
    val gson = Gson()
    var individualRadio = ObservableBoolean(false)
    var companyRadio = ObservableBoolean(false)
    var apiCode = -1 /* 0 -> service location , 1 -> company list */

    var signupServiceLocList: ArrayList<SignupServiceLocData> ?= ArrayList()
    var companyList : List<CompanyModel.Company> = ArrayList()
    var selectedCompanySlug = ""
    var showOtherCompanyOptions = ObservableBoolean(false)
    var companyName = ObservableField("")
    var companyPhone = ObservableField("")
    var companyVehicleNumbers = ObservableField("")
    var lastIndex = 0
    var selectedServiceLocation = ObservableField("")
    var selectedCompany = ObservableField("")
    override fun onSuccessfulApi(response: BaseResponse?) {
        isLoading.value = false
        if (apiCode == 0 && response!!.success as Boolean) {
            val signServiceLocData: BaseResponse.SignupServiceLocation =
                Gson().fromJson(
                    Gson().toJson(response.data),
                    BaseResponse.SignupServiceLocation::class.java
                )
            signupServiceLocList?.clear()
            signupServiceLocList?.addAll(signServiceLocData.serviceLocation!!)
            //companyListCall()
        }else if(apiCode==1){
            response?.let { res ->
                val company : CompanyModel = Gson().fromJson(Gson().toJson(res.data),
                CompanyModel::class.java)
                company.company?.let {
                    companyList = it
                }

            }
        }
    }

    override fun onFailureApi(call: Call<BaseResponse?>?, t: CustomException?) {
        isLoading.value = false
        getNavigator().showMessage(t!!.exception!!)

        if (t.code == 1001)
            getNavigator().showMessage(t.exception!!)
    }

    fun onClickOutSide(view: View?) {
        getNavigator().hideKeyBoard()
    }

    var loginMethod ="INDIVIDUAL"
    fun onClickConfirm() {
//        when {
//            individualRadio.get() -> loginMethod = "INDIVIDUAL"
//            companyRadio.get() -> loginMethod = "COMPANY"
//            else -> loginMethod = ""
//        }
        msg = ""
        if (Utilz.isEmpty(fname.get()!!)) {
            msg = translationModel?.Validate_FirstName?:""
            getNavigator().getBinds().etFname.error = msg
        } else if (!Pattern.matches("^[a-zA-Z]+( [a-zA-Z]+)*$", fname.get())) {
            msg = translationModel?.Validate_FirstName?:""
            getNavigator().getBinds().etFname.error = msg
        }
//        else if (Utilz.isEmpty(lname.get()!!)) {
//            msg = translationModel?.Validate_LastName?:""
//            getNavigator().getBinds().lname.error = msg
//        } else if (!Pattern.matches("^[a-zA-Z]+( [a-zA-Z]+)*$", fname.get())) {
//            msg = translationModel?.Validate_LastName?:""
//            getNavigator().getBinds().lname.error = msg
//        }
        else if (!Utilz.isEmpty(email.get()!!) && !Utilz.isValidEmail(email.get()!!)) {
            msg = translationModel?.text_error_email_valid!!
            getNavigator().getBinds().editEmailSignup.error = msg
        }else if(serviceLocationSlug.get()!!.isEmpty()) {
            msg = translationModel?.txt_select_service_location ?: ""
            getNavigator().showMessage(msg)
        }
//        else if(!individualRadio.get() && !companyRadio.get()) {
//            msg = translationModel?.txt_select_any_category ?: ""
//            getNavigator().showMessage(msg)
//        }
//         else if(companyRadio.get()){
//            if(selectedCompanySlug == "OTHER"){
//                when {
//                    companyName.get()?.trim()?.length==0 ->{
//                        msg = translationModel?.txt_company_name_error?:""
//                        getNavigator().getBinds().companyName.error = msg
//                    }
//                    companyPhone.get()?.trim()?.length==0 ->{
//                        msg = translationModel?.txt_company_phone_error?:""
//                        getNavigator().getBinds().companyPhone.error = msg
//                    }
//                    companyVehicleNumbers.get()?.trim()?.length==0 ->{
//                        msg = translationModel?.txt_company_vehicle_error?:""
//                        getNavigator().getBinds().companyVehicleNumbers.error = msg
//                    }
//                }
//            }else if (selectedCompanySlug.isEmpty()){
//                msg = translationModel?.txt_select_company?:""
//                getNavigator().showMessage(msg)
//            }
//        }
        if (Utilz.isEmpty(msg)) {
            getNavigator().openVehicleFrag()
        } else {
            msg = ""
        }
    }

    fun serviceLocApicall() {
        if (getNavigator().isNetworkConnected()) {
            apiCode = 0
            serviceLocBase()
        } else getNavigator().showNetworkUnAvailable()
    }

    fun companyListCall(){
        if(getNavigator().isNetworkConnected()){
            apiCode = 1
            getCompanyListBase()
        }else
            getNavigator().showNetworkUnAvailable()
    }

    fun onIndividualClick(){
        showOtherCompanyOptions.set(false)
    }

    fun onCompanyClick(){
        if(selectedCompanySlug == "OTHER")
            showOtherCompanyOptions.set(true)
    }

    fun openServiceLocation(){
        signupServiceLocList?.let { getNavigator().serviceLocationSelection(it) }
    }

    fun openCompanySelection(){
        if(companyList.isNotEmpty())
            getNavigator().companySelection()
    }
}