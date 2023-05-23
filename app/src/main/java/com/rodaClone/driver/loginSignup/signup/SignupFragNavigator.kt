package com.rodaClone.driver.loginSignup.signup

import android.content.Context
import com.rodaClone.driver.base.BaseViewOperator
import com.rodaClone.driver.connection.responseModels.CompanyModel
import com.rodaClone.driver.databinding.SignupFragBinding
import com.rodaClone.driver.loginSignup.vehicleFrag.model.SignupServiceLocData

interface SignupFragNavigator : BaseViewOperator {
    fun hideKeyBoard()
    fun getContextAttached(): Context
    fun openVehicleFrag()
    fun serviceLocationSelection(signupServiceLocList: ArrayList<SignupServiceLocData>)
    fun setServiceLocation(sLocation : SignupServiceLocData)
    fun companySelection()
    fun setSelectedCompany(company : CompanyModel.Company)
    fun getBinds() : SignupFragBinding
}