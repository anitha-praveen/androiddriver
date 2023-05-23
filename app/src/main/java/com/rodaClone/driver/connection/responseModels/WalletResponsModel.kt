package com.rodaClone.driver.connection.responseModels

import com.google.gson.annotations.SerializedName

class WalletResponsModel {
    @SerializedName("id")
    var id: Int? = null

    @SerializedName("wallet_id")
    var walletId: Int? = null

    @SerializedName("request_id")
    var requestId: String? = null

    @SerializedName("amount")
    var amount: Double? = null

    @SerializedName("purpose")
    var purpose: String? = null

    @SerializedName("type")
    var type: String? = null

    @SerializedName("user_id")
    var userId: Int? = null

    @SerializedName("created_at")
    var createdAt: String? = null

    @SerializedName("updated_at")
    var updatedAt: String? = null
}