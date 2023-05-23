package com.rodaClone.driver.connection.responseModels

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class NightUploadBoth {

    @SerializedName("instant_image_upload")
    @Expose
    val instantImageUpload: InstantImageUpload? = null

    class InstantImageUpload {

        @SerializedName("driver_upload_image")
        @Expose
        val driverUploadImage: Boolean? = null

        @SerializedName("user_upload_image")
        @Expose
        val userUploadImage: Boolean? = null

    }
}