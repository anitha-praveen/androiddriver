package com.rodaClone.driver.connection.responseModels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SingleHistoryResponse {
    @SerializedName("data")
    @Expose
    var requestRespone:RequestResponseData? = null
}