package com.rodaClone.driver.loginSignup.vehicleFrag

import android.content.Context
import com.rodaClone.driver.base.BaseViewOperator
import com.rodaClone.driver.connection.responseModels.Vehicles
import com.rodaClone.driver.databinding.VehicleIntroFragBinding
import com.rodaClone.driver.loginSignup.vehicleFrag.model.SignupTypeData

interface VehicleNavigator : BaseViewOperator {
    fun hideKeyBoard()
    fun openKeyBoard()
    fun getContextAttached(): Context
    fun loadDataAdapter(signupTypesList: ArrayList<SignupTypeData?>)
    fun vehicleSlug(data: SignupTypeData)
    fun getToHome()
    fun getAct(): Context
    fun selectServiceTypes()
    fun clearLocalSelections()
    fun vehicleModelSelection(modelsList : List<Vehicles.Vehiclemodel>)
    fun setSelectedVehicleModel(vehicleModel : Vehicles.Vehiclemodel)
    fun getBinds() : VehicleIntroFragBinding
}