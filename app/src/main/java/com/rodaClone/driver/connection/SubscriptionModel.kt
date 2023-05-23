package com.rodaClone.driver.connection

import com.google.gson.annotations.SerializedName

data class SubscriptionModel(

    @SerializedName("id")
    var id: Int? = null,
    @SerializedName("slug")
    var slug: String? = null,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("amount")
    var amount: Int? = null,
    @SerializedName("validity")
    var validity: String? = null,
    @SerializedName("description")
    var description: String? = null,
    @SerializedName("deleted_at")
    var deletedAt: String? = null,
    @SerializedName("created_at")
    var createdAt: String? = null,
    @SerializedName("updated_at")
    var updatedAt: String? = null

)