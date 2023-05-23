package com.rodaClone.driver.connection.responseModels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class HistoryModel {
    @SerializedName("data")
    @Expose
    val data: MutableList<History>? = null


    @SerializedName("last_page")
    @Expose
    var lastPage: Int? = null

    class History{
        @SerializedName("data")
        @Expose
        val data: RequestResponseData? = null
    }

}