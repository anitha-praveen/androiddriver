package com.rodaClone.driver.loginSignup.vehicleFrag.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.rodaClone.driver.connection.BaseResponse

class SignupTypeData : BaseResponse() {
    @SerializedName("id")
    @Expose
    val id: Int? = null

    @SerializedName("vehicle_name")
    @Expose
    val vehicleName: String? = null

    @SerializedName("image")
    @Expose
    val image: String? = null

    @SerializedName("capacity")
    @Expose
    val capacity: String? = null

    @SerializedName("created_at")
    @Expose
    val createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    val updatedAt: String? = null

    @SerializedName("status")
    @Expose
    val status: Int? = null

    @SerializedName("deleted_at")
    @Expose
    val deletedAt: Any? = null

    @SerializedName("slug")
    @Expose
    val slug: String? = null

    @SerializedName("service_types")
    @Expose
    val serviceTypes: List<String>? = null
}