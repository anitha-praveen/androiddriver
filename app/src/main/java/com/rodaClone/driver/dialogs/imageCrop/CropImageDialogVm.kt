package com.rodaClone.driver.dialogs.imageCrop

import com.rodaClone.driver.base.BaseVM
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.connection.ConnectionHelper
import com.rodaClone.driver.connection.CustomException
import com.rodaClone.driver.ut.SessionMaintainence
import retrofit2.Call
import javax.inject.Inject

class CropImageDialogVm @Inject constructor(
    val session: SessionMaintainence,
    val mConnect: ConnectionHelper

) : BaseVM<BaseResponse, CropImageNavigator>(session, mConnect) {

    override fun onSuccessfulApi(response: BaseResponse?) {
        isLoading.value = false
    }

    override fun onFailureApi(call: Call<BaseResponse?>?, t: CustomException?) {
        isLoading.value = false
        getNavigator().showMessage(t!!.exception!!)
    }



}