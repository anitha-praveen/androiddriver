package com.rodaClone.driver.connection.responseModels

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class Sos {
    @SerializedName("id")
    @Expose
    val id: Int? = null

    @SerializedName("created_by")
    @Expose
    val createdBy: Any? = null

    @SerializedName("phone_number")
    @Expose
    val phoneNumber: String? = null

    @SerializedName("description")
    @Expose
    val description: String? = null

    @SerializedName("title")
    @Expose
    val title: String? = null

    @SerializedName("created_at")
    @Expose
    val createdAt: String? = null

    @SerializedName("status")
    @Expose
    val status: Int? = null

    @SerializedName("slug")
    @Expose
    val slug: String? = null
}