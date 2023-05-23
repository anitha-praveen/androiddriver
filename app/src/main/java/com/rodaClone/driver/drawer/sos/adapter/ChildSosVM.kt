package com.rodaClone.driver.drawer.sos.adapter

import com.rodaClone.driver.base.BaseViewOperator
import com.rodaClone.driver.connection.responseModels.Sos

class ChildSosVM(
    val sos: Sos,
    private val adapterLister: ChildSosItemListener) {
    var title = sos.title
    var number = sos.phoneNumber
    var showDelete = sos.createdBy != null
    fun onItemSelected() {
        adapterLister.itemSelected(number!!)
    }

    fun onItemDeleted(){
        adapterLister.itemDeleted("${sos.slug}")
    }

    interface ChildSosItemListener : BaseViewOperator {
        fun itemSelected(sos: String)
        fun itemDeleted(slug:String)

    }

}
