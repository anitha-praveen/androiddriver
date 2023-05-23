package com.rodaClone.driver.connection.responseModels

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class Languages {
    @SerializedName("id")
    @Expose
    val id: Int? = null

    @SerializedName("name")
    @Expose
    val name: String? = null

    @SerializedName("code")
    @Expose
    val code: String? = null

    @SerializedName("created_at")
    @Expose
    val createdAt: Any? = null

    @SerializedName("updated_at")
    @Expose
    val updatedAt: String? = null

    @SerializedName("updated_date")
    @Expose
    val updatedDate: Int? = null
}