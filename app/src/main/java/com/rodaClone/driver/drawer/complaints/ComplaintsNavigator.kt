package com.rodaClone.driver.drawer.complaints

import android.content.Context
import com.rodaClone.driver.base.BaseViewOperator
import com.rodaClone.driver.connection.responseModels.Complaint
import com.rodaClone.driver.databinding.FragmentComplaintsBinding

interface ComplaintsNavigator : BaseViewOperator {
    fun addItems(data: List<Complaint>)
    fun setSelected(data:Complaint)
    fun getCtx(): Context
    fun getBinds(): FragmentComplaintsBinding
    fun hideKeyFomNav()
}