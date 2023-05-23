package com.rodaClone.driver.loginSignup.getStarteedScrn

import com.rodaClone.driver.base.BaseViewOperator
import com.rodaClone.driver.connection.responseModels.Languages

interface GetStartedScreenNavigator : BaseViewOperator {
    fun setSelectedLanguage(languages: Languages)
    fun initiateNaviagation()
}