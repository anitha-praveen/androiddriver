package com.rodaClone.driver.connection.responseModels

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import com.rodaClone.driver.connection.BaseResponse
import java.io.Serializable

/**
 * Used same class for request end Api and Request receieve api.
 */
class RequestResponseData : Serializable {

    /**
     * this is for request end Api.
     * Why we used here is - In base class we have more than 1 data object class, so that i splitted onto seperate class.
     */
    @SerializedName("requestBill")
    @Expose
    val requestBill: BaseResponse.RequestBillDataResponse? = null


    @SerializedName("id")
    @Expose
    val id: String? = null

    @SerializedName("request_number")
    @Expose
    val requestNumber: String? = null

    @SerializedName("vehicle_name")
    @Expose
    val vehName: String? = null


    @SerializedName("request_otp")
    @Expose
    val requestOtp: Int? = null

    @SerializedName("is_later")
    @Expose
    val isLater: Int? = null

    @SerializedName("user_id")
    @Expose
    val userId: Int? = null

    @SerializedName("if_dispatch")
    @Expose
    val ifDispatch: Int? = null

    @SerializedName("trip_start_time")
    @Expose
    val tripStartTime: String? = null

    @SerializedName("is_driver_started")
    @Expose
    val isDriverStarted: Int? = null

    @SerializedName("is_driver_arrived")
    @Expose
    val isDriverArrived: Int? = null

    @SerializedName("is_trip_start")
    @Expose
    val isTripStart: Int? = null

    @SerializedName("total_distance")
    @Expose
    val totalDistance: Double? = null

    @SerializedName("total_time")
    @Expose
    val totalTime: String? = null

    @SerializedName("is_completed")
    @Expose
    val isCompleted: Int? = null

    @SerializedName("driver_time_out")
    @Expose
    val driverTimeOut: String? = null


    @SerializedName("is_cancelled")
    @Expose
    val isCancelled: Int? = null

    @SerializedName("completed_at")
    @Expose
    val completedAt:String? = null

    @SerializedName("cancel_method")
    @Expose
    val cancelMethod: String? = null

    @SerializedName("payment_opt")
    @Expose
    val paymentOpt: String? = null

    @SerializedName("is_paid")
    @Expose
    val isPaid: Int? = null

    @SerializedName("user_rated")
    @Expose
    val userRated: Int? = null

    @SerializedName("driver_rated")
    @Expose
    val driverRated: Int? = null

    @SerializedName("unit")
    @Expose
    val unit: String? = null

    @SerializedName("zone_type_id")
    @Expose
    val zoneTypeId: Int? = null

    @SerializedName("pick_lat")
    @Expose
    val pickLat: Double? = null

    @SerializedName("pick_lng")
    @Expose
    val pickLng: Double? = null

    @SerializedName("drop_lat")
    @Expose
    val dropLat: Double? = null

    @SerializedName("drop_lng")
    @Expose
    val dropLng: Double? = null

    @SerializedName("pick_address")
    @Expose
    val pickAddress: String? = null

    @SerializedName("drop_address")
    @Expose
    val dropAddress: String? = null

    @SerializedName("stops")
    @Expose
    val stops: Stops? = null

    @SerializedName("requested_currency_code")
    @Expose
    val requestedCurrencyCode: String? = null

    @SerializedName("requested_currency_symbol")
    @Expose
    val requestedCurrencySymbol: String? = null

    @SerializedName("is_instant_trip")
    @Expose
    val is_instant_trip: Int? = null


    @SerializedName("user_overall_rating")
    @Expose
    val userOverallRating: Double? = null

    @SerializedName("location_approve")
    @Expose
    val location_approve: Int? = null

    @SerializedName("poly_string")
    @Expose
    val polyString: String? = null


    @SerializedName("user")
    @Expose
    val user: RequestUserData? = null

    @SerializedName("driver_notes")
    @Expose
    val driver_notes:String? = null

    @SerializedName("user_upload_image")
    @Expose
    val userUploadImage : Boolean? = null

    @SerializedName("driver_upload_image")
    @Expose
    val driverUploadImage : Boolean? = null

    @SerializedName("skip_night_photo")
    @Expose
    val skipNightPhoto : Boolean? = null

    @SerializedName("start_night_time")
    @Expose
    val startNightTime : String? = null

    @SerializedName("end_night_time")
    @Expose
    val endNightTime : String? = null

    /***
     * re directed to request data because same params is received
     */
    @SerializedName("driver")
    @Expose
    val driver: BaseResponse.ReqDriverData? = null


    @SerializedName("grace_waiting_time")
    @Expose
    val graceWaitingTime: String? = null

    @SerializedName("grace_waiting_time_after_start")
    @Expose
    val graceWaitingTimeAfterStart: String? = null

    @SerializedName("outstation_trip_type")
    @Expose
    val outstationTripType :String? = null

    @SerializedName("trip_end_time")
    @Expose
    val tripEndtime :String? = null

    @SerializedName("dispute_status")
    @Expose
    val disputeStatus: Int? = null

    @SerializedName("instant_phone_number")
    val instant_phone_number: String? = null

    @SerializedName("last_page")
    @Expose
    var lastPage: Int? = null

    @SerializedName("service_category")
    @Expose
    var serviceCategory: String? = null

    @SerializedName("package_hour")
    @Expose
    val packageHour: String? = null

    @SerializedName("package_km")
    @Expose
    val packageKm: String? = null

    @SerializedName("start_km")
    @Expose
    val startKM: String? = null

    @SerializedName("end_km")
    @Expose
    val endKM: String? = null

    @SerializedName("others")
    @Expose
    val others : Others ? = null


}

data class Stops(
    @SerializedName("address")
    @Expose
    val address: String? = null,

    @SerializedName("latitude")
    @Expose
    val latitude: String? = null,

    @SerializedName("longitude")
    @Expose
    val longitude: String? = null,
) : Serializable

data class Others(
    @SerializedName("name")
    @Expose
    val name : String? = null,

    @SerializedName("phone_number")
    @Expose
    val phoneNumber : String? = null
) : Serializable

