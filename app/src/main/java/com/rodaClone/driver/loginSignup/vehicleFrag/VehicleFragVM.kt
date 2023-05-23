package com.rodaClone.driver.loginSignup.vehicleFrag

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.google.gson.Gson
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseVM
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.connection.ConnectionHelper
import com.rodaClone.driver.connection.CustomException
import com.rodaClone.driver.connection.responseModels.Vehicles
import com.rodaClone.driver.loginSignup.SignupData
import com.rodaClone.driver.loginSignup.vehicleFrag.model.SignupTypeData
import com.rodaClone.driver.ut.Config
import com.rodaClone.driver.ut.SessionMaintainence
import com.rodaClone.driver.ut.Utilz
import retrofit2.Call
import java.util.regex.Pattern
import javax.inject.Inject


class VehicleFragVM @Inject constructor(
    val session: SessionMaintainence,
    val mConnect: ConnectionHelper

) :
    BaseVM<BaseResponse, VehicleNavigator>(session, mConnect) {
    var edit_vehiclenumber_register = ObservableField("")

    var countrycode = ObservableField("")
    var email = ObservableField("")
    var phonenumber = ObservableField("")
    var selectedVehicleTypeData = SignupTypeData()
    var msg: String = ""
    var map: HashMap<String, String> = HashMap()
    var showOtherVehicleModelOptions = ObservableBoolean(false)
    var selectedVehicleModelSlug = ""
    var modelName =
        ObservableField("") /* this is used if other option selected in vehicle model spinner */
    var selectedServices = ObservableField("")
    var signupTypesList: ArrayList<SignupTypeData?> = ArrayList()
    var modelsList: List<Vehicles.Vehiclemodel> = ArrayList()
    val gson = Gson()
    var calledApi: Int = -1   /* 0 -> types , 1 -> signup , 2 -> auth token , 3 -> vehicle model  */
    var isAnyVehicleTypeChosen = ObservableBoolean(false)
    var showSelectService = ObservableBoolean(false)
    var showBrandLabel = ObservableBoolean(false)
    var dontShowBrandLabel = ObservableBoolean(false)
    var selectedVehicleModel = ObservableField("")
    override fun onSuccessfulApi(response: BaseResponse?) {
        isLoading.value = false
        response?.let { res ->
            when (calledApi) {
                0 -> {
                    val signData: BaseResponse.DataObjectsAllApi? =
                        Gson().fromJson(
                            Gson().toJson(res.data),
                            BaseResponse.DataObjectsAllApi::class.java
                        )
                    signupTypesList.clear()
                    signData?.types?.let { signupTypesList.addAll(it) }
                    getNavigator().loadDataAdapter(signupTypesList)
                }
                1 -> {
                    val signRespData: BaseResponse.SignupResponseData? =
                        Gson().fromJson(
                            Gson().toJson(res.data),
                            BaseResponse.SignupResponseData::class.java
                        )
                    signRespData?.clientId?.let { id ->
                        signRespData.clientSecret?.let { token ->
                            authTokenApi(id, token)
                        }
                    }
                }
                2 -> {
                    res.accessToken?.let {
                        session.saveString(SessionMaintainence.AccessToken, it)
                        getNavigator().getToHome()
                    }
                }
                3 -> {
                    val vehicle: Vehicles =
                        Gson().fromJson(Gson().toJson(response.data), Vehicles::class.java)
                    vehicle.vehiclemodel?.let {
                        modelsList = it
                    }
                }
                else -> {
                    return
                }
            }
        }
    }

    override fun onFailureApi(call: Call<BaseResponse?>?, t: CustomException?) {
        isLoading.value = false
        if (t!!.code == 1001)
            showConfirmAlert(t.exception!!)
        else
            getNavigator().showMessage(t.exception!!)
    }


    fun typesApiCall() {
        if (getNavigator().isNetworkConnected()) {
            calledApi = 0
            typesBase()
        } else getNavigator().showNetworkUnAvailable()
    }

    fun onClickConfirm(view: View) {
        msg = ""
        if (selectedVehicleTypeData.slug == null) {
            msg = translationModel?.txt_select_vehicle_type ?: ""
            getNavigator().showMessage(msg)
        } else if (selectedVehicleModelSlug != "OTHER" && selectedVehicleModelSlug.isEmpty()) {
            msg = translationModel?.hint_vehicle_model ?: ""
            getNavigator().showMessage(msg)
        } else if (selectedVehicleModelSlug == "OTHER" && modelName.get()?.trim()!!.isEmpty()) {
            msg = translationModel?.txt_enter_veh_model_name ?: ""
            getNavigator().getBinds().modelName.error = msg
        } else if (edit_vehiclenumber_register.get()!!.trim().isEmpty()) {
            msg = translationModel?.txt_enter_veh_number ?: ""
            getNavigator().getBinds().editVehiclenoRegister.error = msg
        } else if (!(vehicleValidation(edit_vehiclenumber_register.get()!!.trim()))) {
            msg = translationModel?.txt_enter_valid_veh_number ?: ""
            getNavigator().getBinds().editVehiclenoRegister.error = msg
        }else if (showSelectService.get() && selectedServices.get()?.isEmpty()!!) {
            msg = translationModel?.txt_select_service_types ?: ""
            getNavigator().showMessage(msg)
        }else if (showSelectService.get() && selectedServices.get()?.isEmpty()!!) {
            msg = translationModel?.txt_select_service_types ?: ""
            getNavigator().showMessage(msg)
        }

        if (Utilz.isEmpty(msg)) {
            callSignup()
        } else {
            msg = ""
        }

    }

    private fun authTokenApi(id: String, token: String) {
        if (getNavigator().isNetworkConnected()) {
            calledApi = 2
            map.clear()
            map[Config.grant_type] = "client_credentials"
            map[Config.client_id] = id
            map[Config.client_secret] = token
            authTokenCallBase(map)
        } else getNavigator().showNetworkUnAvailable()
    }

    fun getVehicleModels(slug: String) {
        if (getNavigator().isNetworkConnected()) {
            calledApi = 3
            map.clear()
            map[Config.slug] = slug
            getVehicleModelsBase(map)
        } else getNavigator().showNetworkUnAvailable()
    }

    private fun showConfirmAlert(message: String) {
        val dialog = Dialog(getNavigator().getAct())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.cancel_alert_lay)


        val subHeading = dialog.findViewById<TextView>(R.id.questionText)
        subHeading.setText(message)

        val yes: AppCompatButton = dialog.findViewById(R.id.yesButton)
        yes.text = translationModel?.text_yes

        val no: AppCompatButton = dialog.findViewById(R.id.noButton)
        no.text = translationModel?.text_no

        yes.setOnClickListener {
            if (getNavigator().isNetworkConnected()) {
                map.clear()
                map[Config.phone_number] = SignupData.phoneNumber
                map[Config.country_code] = SignupData.countrycodeID
                if (SignupData.email.isNotEmpty())
                    map[Config.email] = SignupData.email
                map[Config.firstname] = SignupData.fname
                map[Config.lastname] = SignupData.lastname
                map[Config.device_info_hash] = SignupData.device_info_hash
                map[Config.device_type] = SignupData.device_type
                map[Config.is_primary] = "1"
                map[Config.login_method] = SignupData.login_method
                if (SignupData.isCompanySelected) {
                    if (SignupData.isOtherCompany) {
                        map[Config.company_name] = SignupData.company_name
                        map[Config.company_no_of_vehicles] = SignupData.company_no_of_vehicles
                        map[Config.company_phone] = "+91${SignupData.company_phone}"
                    } else {
                        map[Config.company_slug] = SignupData.company_slug
                    }
                }
                map[Config.service_location] = SignupData.service_location
                map[Config.vehicle_type_slug] = selectedVehicleTypeData.slug!!
                if (selectedVehicleModelSlug == "OTHER") {
                    map[Config.vehicle_model_name] = modelName.get()!!
                } else {
                    map[Config.vehicle_model_slug] = selectedVehicleModelSlug
                }
                map[Config.service_category] = selectedServices.get()!!.replace(" ", "")
                map[Config.brand_label] = if (showBrandLabel.get()) "YES" else "NO"
                map[Config.car_number] = edit_vehiclenumber_register.get()!!
                if (!SignupData.referral_code.isEmpty()) {
                    map[Config.referral_code] = SignupData.referral_code
                }

                calledApi = 1
                signUpBase(map)

            } else
                getNavigator().showNetworkUnAvailable()
        }

        no.setOnClickListener { dialog.dismiss() }
        dialog.show()

    }

    private fun callSignup() {
        if (getNavigator().isNetworkConnected()) {
            map.clear()
            map[Config.phone_number] = SignupData.phoneNumber
            map[Config.country_code] = SignupData.countrycodeID
            if (SignupData.email.isNotEmpty())
                map[Config.email] = SignupData.email
            map[Config.firstname] = SignupData.fname
            map[Config.lastname] = SignupData.lastname
            map[Config.device_info_hash] = SignupData.device_info_hash
            map[Config.device_type] = SignupData.device_type
            map[Config.is_primary] = "1"
            map[Config.login_method] = SignupData.login_method
            if (SignupData.isCompanySelected) {
                if (SignupData.isOtherCompany) {
                    map[Config.company_name] = SignupData.company_name
                    map[Config.company_no_of_vehicles] = SignupData.company_no_of_vehicles
                    map[Config.company_phone] = "+91${SignupData.company_phone}"
                } else {
                    map[Config.company_slug] = SignupData.company_slug
                }
            }
            map[Config.service_location] = SignupData.service_location
            map[Config.vehicle_type_slug] = selectedVehicleTypeData.slug!!
            if (selectedVehicleModelSlug == "OTHER") {
                map[Config.vehicle_model_name] = modelName.get()!!
            } else {
                map[Config.vehicle_model_slug] = selectedVehicleModelSlug
            }
            map[Config.service_category] = selectedServices.get()!!.replace(" ", "")
            map[Config.brand_label] = if (showBrandLabel.get()) "YES" else "NO"
            map[Config.car_number] = edit_vehiclenumber_register.get()!!
            if (!SignupData.referral_code.isEmpty()) {
                map[Config.referral_code] = SignupData.referral_code
            }

            calledApi = 1
            signUpBase(map)
        } else
            getNavigator().showNetworkUnAvailable()

    }

    fun onClickServiceType() {
        getNavigator().selectServiceTypes()
    }

    private fun vehicleValidation(data: String): Boolean {
        val p = Pattern.compile("^[a-zA-Z0-9_.-]*\$")
        val m = p.matcher(data)
        return m.find() && m.group() == data
    }


    fun openVehicleModelSelection() {
        if (modelsList.isNotEmpty())
            getNavigator().vehicleModelSelection(modelsList)
    }

    fun hideKeyBoardIfOpen() {
        getNavigator().hideKeyBoard()
    }
}