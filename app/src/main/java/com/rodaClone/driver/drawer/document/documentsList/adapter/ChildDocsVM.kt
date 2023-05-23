package com.rodaClone.driver.drawer.document.documentsList.adapter

import android.view.View
import androidx.databinding.ObservableBoolean
import com.rodaClone.driver.base.BaseViewOperator
import com.rodaClone.driver.connection.TranslationModel
import com.rodaClone.driver.connection.responseModels.GroupDocument

class ChildDocsVM (
    val document: GroupDocument,
    val adapterLister: ChildDocumentItemListener,
    val translationModel:TranslationModel,
    val isRequired:Boolean,
    val showExpiredDenied:Boolean,
    val expiredDenied:String
) {
    var isUploaded = document.uploadStatus == 1
    var documentName = document.name


    fun onItemSelected(view: View) {
        adapterLister.itemSelected(document)
    }

    interface ChildDocumentItemListener : BaseViewOperator {
        fun itemSelected(document: GroupDocument)
    }


}
