package com.rodaClone.driver.drawer.profile

import android.content.Context
import com.rodaClone.driver.base.BaseViewOperator
import com.rodaClone.driver.connection.responseModels.Languages

interface ProfileFragNavigator : BaseViewOperator {
    fun setImage(url : String)
    fun openVehicleInfo()
    fun getCtx(): Context
    fun alertSelectCameraGallery()
    fun openEditProfile(mode:Int,value: String)
    fun openLanguageSelection()
    fun setSelectedLanguage(languages: Languages)
    fun refresh()
    fun changeProfileDetails(fName:String,lName:String,driverProfile:String)
    fun showProfileImg(url: String)
}