package com.rodaClone.driver.loginSignup.login

import android.content.Context
import com.rodaClone.driver.base.BaseViewOperator

interface LoginNavigator : BaseViewOperator {
    fun hideKeyBoard()
    fun getContextAttached() : Context
    fun openCountryListDialogue()
    fun openOtp(phone: String , cc : String ,ccId : String)
//    fun state():Boolean
    fun setupCountryList()
}