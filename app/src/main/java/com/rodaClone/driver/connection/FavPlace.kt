package com.rodaClone.driver.connection

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FavPlace {

    @SerializedName("FavouriteList")
    @Expose
     val favouriteList: List<Favourite>? = null

    class Favourite{
        @SerializedName("id")
        @Expose
        val id: Int? = null

        @SerializedName("user_id")
        @Expose
        val userId: Int? = null

        @SerializedName("title")
        @Expose
        var title: String? = null

        @SerializedName("latitude")
        @Expose
        val latitude: String? = null

        @SerializedName("longitude")
        @Expose
        val longitude: String? = null

        @SerializedName("address")
        @Expose
        var address: String? = null

        @SerializedName("status")
        @Expose
        val status: Int? = null

        @SerializedName("slug")
        @Expose
        val slug: String? = null

    }




}