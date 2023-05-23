package com.rodaClone.driver.drawer.document.documentsList

import com.rodaClone.driver.base.BaseViewOperator
import com.rodaClone.driver.connection.responseModels.GroupDocument

interface DocumentsListNavigator : BaseViewOperator{
    fun onDocumentSelected(document: GroupDocument)
    fun addList(documents : List< GroupDocument>?)
    fun callReqProgDrawerAct()
}