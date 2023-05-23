package com.rodaClone.driver.drawer.language

import com.rodaClone.driver.base.BaseViewOperator
import com.rodaClone.driver.connection.responseModels.Languages


interface LanguageNavigator : BaseViewOperator {
    fun setSelectedLanguage(languages: Languages)
    fun refresh()
    fun close()
}