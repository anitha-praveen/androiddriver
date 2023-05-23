package com.rodaClone.driver.drawer.invoice

import com.rodaClone.driver.base.BaseViewOperator

interface InvoiceNavigator : BaseViewOperator {
    fun chooseDirection()
    fun gotoComplaints()
}