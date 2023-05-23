package com.rodaClone.driver.drawer.document.uploadDocument.adapter

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.rodaClone.driver.connection.responseModels.GetDocument

class DcuploadImageItemVm(val document: GetDocument, private val adapter:DcImageSelected,private val position: Int) {
    val title = ObservableField(document.documentName)
    val showRequired = ObservableBoolean( document.requried == 1)

    fun onImageSelected(){
        adapter.onItemSelected(document,position)
    }

    interface DcImageSelected {
        fun onItemSelected(document: GetDocument,position: Int)
    }
}

