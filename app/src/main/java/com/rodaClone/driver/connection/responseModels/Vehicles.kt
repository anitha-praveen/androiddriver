package com.rodaClone.driver.connection.responseModels

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class Vehicles {
    @SerializedName("vehiclemodel")
    @Expose
    val vehiclemodel: List<Vehiclemodel>? = null

    class Vehiclemodel{

        @SerializedName("model_name")
        @Expose
        var modelName: String? = null

        @SerializedName("slug")
        @Expose
        var slug : String? = null
    }
}