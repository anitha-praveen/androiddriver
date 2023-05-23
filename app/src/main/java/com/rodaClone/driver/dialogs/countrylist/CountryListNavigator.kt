package com.rodaClone.driver.dialogs.countrylist

import com.rodaClone.driver.base.BaseViewOperator
import com.rodaClone.driver.connection.responseModels.Country

interface CountryListNavigator : BaseViewOperator {
    fun clickedItem(country: Country)
    fun close()
    fun itemSize(size: Int)
}