package com.rodaClone.driver.loginSignup.otp

import android.app.Activity
import android.content.Context
import com.rodaClone.driver.base.BaseViewOperator

interface OtpNavigator : BaseViewOperator {
    fun verifyCode(otp: String?)
    fun getOpt(): String?
    fun goToSignUp()
    fun goToDrawer()
    fun getAct(): Activity
    fun bacpressed()
    fun getCtx() : Context?
    fun setAllowBackNav(value : Boolean)
    fun callHideKeyBoard()
}