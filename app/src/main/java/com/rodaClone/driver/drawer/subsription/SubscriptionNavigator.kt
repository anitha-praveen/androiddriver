package com.rodaClone.driver.drawer.subsription

import com.rodaClone.driver.base.BaseViewOperator
import com.rodaClone.driver.connection.SubscriptionModel
import java.util.ArrayList


interface SubscriptionNavigator : BaseViewOperator {
    fun setAdapter(list: ArrayList<SubscriptionModel>)
    fun setImage(url: String)
    fun clickedItem(id: String?)
}