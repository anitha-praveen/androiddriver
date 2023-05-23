package com.rodaClone.driver.drawer.approvalDecline

import com.rodaClone.driver.base.BaseViewOperator

interface ApprovalNavigator : BaseViewOperator{
    fun handleClick()
    fun openSubscription()
    fun openSideMenu()
}