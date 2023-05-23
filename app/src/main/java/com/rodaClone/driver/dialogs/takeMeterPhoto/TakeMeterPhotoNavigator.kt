package com.rodaClone.driver.dialogs.takeMeterPhoto

import android.content.Context
import com.rodaClone.driver.base.BaseViewOperator

interface TakeMeterPhotoNavigator:BaseViewOperator {
    fun openCamera()
    fun getCtx():Context?
    fun dissmissFrag()
}