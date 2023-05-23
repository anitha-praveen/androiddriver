package com.rodaClone.driver.connection.responseModels

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class Complaint {
    @SerializedName("id")
    @Expose
    val id: Int? = null

    @SerializedName("title")
    @Expose
    val title: String? = null

    @SerializedName("slug")
    @Expose
    val slug: String? = null

    var isSelected : Boolean = false

}