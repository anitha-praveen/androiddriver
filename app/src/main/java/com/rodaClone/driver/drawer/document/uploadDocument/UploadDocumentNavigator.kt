package com.rodaClone.driver.drawer.document.uploadDocument

import android.content.Context
import com.rodaClone.driver.base.BaseViewOperator
import com.rodaClone.driver.connection.responseModels.GetDocument

interface UploadDocumentNavigator : BaseViewOperator {
    fun getCtx() : Context?
    fun cameraIntent()
    fun galleryIntent()
    fun openGalleryorCamera(document: GetDocument,position:Int)
    fun notifyAdapterImage(file:String)
    fun goToDocumentList()
    fun removeImages(document: ArrayList<GetDocument>)
}