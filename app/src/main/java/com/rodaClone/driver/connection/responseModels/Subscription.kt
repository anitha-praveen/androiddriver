package com.rodaClone.driver.connection.responseModels

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class Subscription {
    @SerializedName("total_days")
    @Expose
    val totalDays: String? = null

    @SerializedName("balance_days")
    @Expose
    val balanceDays: String? = null

    @SerializedName("from_date")
    @Expose
    val fromDate: String? = null

    @SerializedName("to_date")
    @Expose
    val toDate: String? = null

    @SerializedName("paid_status")
    @Expose
    val paidStatus: Int? = null

    @SerializedName("amount")
    @Expose
    val amount: Int? = null

    @SerializedName("name")
    @Expose
    val name: String? = null
}