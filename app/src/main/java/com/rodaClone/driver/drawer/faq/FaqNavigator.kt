package com.rodaClone.driver.drawer.faq

import com.rodaClone.driver.base.BaseViewOperator
import com.rodaClone.driver.connection.responseModels.Faq

interface FaqNavigator : BaseViewOperator {
    fun setFaqAdapter(list: ArrayList<Faq>)
}