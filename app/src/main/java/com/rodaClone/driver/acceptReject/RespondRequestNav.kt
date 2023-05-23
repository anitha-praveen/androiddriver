package com.rodaClone.driver.acceptReject

import android.content.Context
import com.rodaClone.driver.base.BaseViewOperator

interface RespondRequestNav : BaseViewOperator {
    fun getCtx() : Context
    fun updateProgressBar(progress : Int)
    fun setMax(max : Int)
    fun callReqProgress()
}