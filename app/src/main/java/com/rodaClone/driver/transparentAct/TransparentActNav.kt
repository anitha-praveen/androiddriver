package com.rodaClone.driver.transparentAct

import android.content.Context
import com.rodaClone.driver.base.BaseViewOperator

interface TransparentActNav: BaseViewOperator {
    fun getCtx() : Context
    fun setProg(pro : Int)
    fun setMax(max : Int)
}