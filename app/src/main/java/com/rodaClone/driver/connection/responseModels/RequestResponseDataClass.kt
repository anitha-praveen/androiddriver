package com.rodaClone.driver.connection.responseModels

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import com.rodaClone.driver.connection.BaseResponse
import java.io.Serializable


class RequestResponseDataClass : Serializable {
    @SerializedName("id")
    @Expose
    val id: Int? = null

    @SerializedName("base_price")
    @Expose
    val basePrice: Double? = null

    @SerializedName("base_distance")
    @Expose
    val baseDistance: Int? = null

    @SerializedName("price_per_distance")
    @Expose
    val pricePerDistance: Double? = null

    @SerializedName("distance_price")
    @Expose
    val distancePrice: Double? = null

    @SerializedName("price_per_time")
    @Expose
    val pricePerTime: Double? = null

    @SerializedName("time_price")
    @Expose
    val timePrice: Double? = null

    @SerializedName("waiting_charge")
    @Expose
    val waitingCharge: Double? = null

    @SerializedName("cancellation_fee")
    @Expose
    val cancellationFee: Double? = null

    @SerializedName("admin_commision")
    @Expose
    val adminCommision: Double? = null

    @SerializedName("driver_commision")
    @Expose
    val driverCommision: Double? = null

    @SerializedName("total_amount")
    @Expose
    val totalAmount: Double? = null

    @SerializedName("booking_fees")
    @Expose
    val bookingFee: Double? = null

    @SerializedName("total_distance")
    @Expose
    val totalDistance: Double? = null

    @SerializedName("total_time")
    @Expose
    val totalTime: Int? = null

    @SerializedName("promo_discount")
    @Expose
    val promoDiscount: Double? = null

    @SerializedName("requested_currency_code")
    @Expose
    val requestedCurrencyCode: String? = null

    @SerializedName("requested_currency_symbol")
    @Expose
    val requestedCurrencySymbol: String? = null

    @SerializedName("out_of_zone_price")
    @Expose
    val outZonePrice: Int? = null

    @SerializedName("service_tax")
    val serviceTax: Double? = null

    @SerializedName("hill_station_price")
    val hillStationPrice: Double? = null

    @SerializedName("user")
    @Expose
    val user: RequestUserData? = null

    @SerializedName("driver")
    @Expose
    val driver: BaseResponse.ReqDriverData? = null

    @SerializedName("pending_km")
    @Expose
    val pendingKm : String? = null

    @SerializedName("package_hours")
    @Expose
    val packageHours: String? = null

    @SerializedName("package_km")
    @Expose
    val packageKm: String? = null


}