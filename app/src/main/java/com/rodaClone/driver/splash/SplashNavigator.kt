package com.rodaClone.driver.splash

import com.rodaClone.driver.base.BaseViewOperator
import com.rodaClone.driver.connection.responseModels.AvailableCountryAndKLang

interface SplashNavigator : BaseViewOperator {
    fun startRequestingPermission()
    fun translationForCurrentLanguage(model: AvailableCountryAndKLang)
    fun openAppUpdateDialog()
}