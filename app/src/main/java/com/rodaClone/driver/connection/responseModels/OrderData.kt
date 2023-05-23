package com.rodaClone.driver.connection.responseModels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class OrderData {
    @SerializedName("user_id")
    @Expose
    val user_id: Int? = null

    @SerializedName("order_id")
    @Expose
    val order_id: String? = null

    @SerializedName("currency")
    @Expose
    val currency: String? = null

    @SerializedName("amount")
    @Expose
    val amount: String? = null

    @SerializedName("key_id")
    @Expose
    val key_id: String? = null

}