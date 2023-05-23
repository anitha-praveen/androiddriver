package com.rodaClone.driver.connection.responseModels

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class CompanyModel {

    @SerializedName("company")
    @Expose
    val company: List<Company>? = null

    class Company {
        @SerializedName("id")
        @Expose
        val id: Int? = null

        @SerializedName("slug")
        @Expose
        var slug: String? = null

        @SerializedName("firstname")
        @Expose
        var firstname: String? = null
    }
}