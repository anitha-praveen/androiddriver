package com.rodaClone.driver.connection.responseModels

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class DriverModel : Serializable{
    @SerializedName("user")
    @Expose
    val user: Driver? = null

    class Driver : Serializable{
        @SerializedName("slug")
        @Expose
        val slug: String? = null

        @SerializedName("firstname")
        @Expose
        val firstname: String? = null

        @SerializedName("lastname")
        @Expose
        val lastname: String? = null

        @SerializedName("email")
        @Expose
        val email: String? = null

        @SerializedName("phone_number")
        @Expose
        val phoneNumber: String? = null

        @SerializedName("currency")
        @Expose
        val currency: String? = null

        @SerializedName("country")
        @Expose
        val country: String? = null

        @SerializedName("profile_pic")
        @Expose
        val profilePic: String? = null

        @SerializedName("car_details")
        @Expose
        val carDetails: CarDetails? = null
    }

    class CarDetails : Serializable {
        @SerializedName("car_number")
        @Expose
        val carNumber: String? = null

        @SerializedName("car_model")
        @Expose
        val carModel: String? = null

        @SerializedName("car_year")
        @Expose
        val carYear: String? = null

        @SerializedName("car_colour")
        @Expose
        val carColour: String? = null

        @SerializedName("zone_name")
        @Expose
        val zoneName: String? = null

        @SerializedName("vehicle_type")
        @Expose
        val vehicleType: String? = null
    }

}