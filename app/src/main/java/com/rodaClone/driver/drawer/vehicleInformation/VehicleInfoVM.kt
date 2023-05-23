package com.rodaClone.driver.drawer.vehicleInformation

import android.app.Application
import androidx.databinding.ObservableField
import com.rodaClone.driver.base.BaseVM
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.connection.ConnectionHelper
import com.rodaClone.driver.connection.CustomException
import com.rodaClone.driver.connection.responseModels.DriverModel
import com.rodaClone.driver.ut.SessionMaintainence
import retrofit2.Call
import javax.inject.Inject

class VehicleInfoVM @Inject constructor(
    val application: Application,
    val session: SessionMaintainence,
    val mConnect: ConnectionHelper
) :
    BaseVM<BaseResponse, VehicleInfoNavigator>(session, mConnect) {
    lateinit var carDetails: DriverModel.CarDetails

    var vehType = ObservableField("")
    var vehNumber = ObservableField("")
    var vehModel = ObservableField("")
    var vehYear = ObservableField("")
    var vehColor = ObservableField("")
    var zoneName = ObservableField("")

    override fun onSuccessfulApi(response: BaseResponse?) {
        isLoading.value = false

    }

    override fun onFailureApi(call: Call<BaseResponse?>?, t: CustomException?) {
        isLoading.value = false
        getNavigator().showMessage(t!!.exception!!)
    }

    fun setUpDetails(){
        if(carDetails.vehicleType != null)
            vehType.set(carDetails.vehicleType)
        if(carDetails.carNumber != null)
            vehNumber.set(carDetails.carNumber)
        if(carDetails.carModel!=null)
            vehModel.set(carDetails.carModel)
        if(carDetails.carYear!=null)
            vehYear.set(carDetails.carYear)
        if(carDetails.carColour!=null)
            vehColor.set(carDetails.carColour)
        if(carDetails.zoneName != null)
            zoneName.set(carDetails.zoneName)
    }

}