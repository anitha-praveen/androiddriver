package com.rodaClone.driver.connection.responseModels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class DashboardModel : Serializable {

    @SerializedName("currency")
    @Expose
    val currency: String? = null

    @SerializedName("today_trips")
    @Expose
    val todayTrips: TodayTrips? = null

    @SerializedName("yesterday_trips")
    @Expose
    val yesterdayTrips: YesterdayTrips? = null

    @SerializedName("weekly_trips")
    @Expose
    val weeklyTrips: WeeklyTrips? = null

    @SerializedName("monthly_trips")
    @Expose
    val monthlyTrips: MonthlyTrips? = null

    @SerializedName("total_trips")
    @Expose
    val totalTrips: TotalTrips? = null

    @SerializedName("rating")
    @Expose
    val rating: Double? = null

    @SerializedName("accept_ratio")
    @Expose
    val acceptRatio: Double? = null

    @SerializedName("fine_amount")
    @Expose
    val fineAmount: String? = null

    @SerializedName("cancellation_fee_amount")
    @Expose
    val cancellationFeeAmount: String? = null

    @SerializedName("cancellation_earn_amount")
    @Expose
    val cancellationEarnAmount: String? = null

    @SerializedName("balance_cancellation_amount")
    @Expose
    val balanceCancellationAmount: String? = null

    @SerializedName("report")
    @Expose
    val report: Report? = null

    class Amount : Serializable {
        @SerializedName("cash")
        @Expose
        val cash: Double? = null

        @SerializedName("card")
        @Expose
        val card: Double? = null

        @SerializedName("wallet")
        @Expose
        val wallet: Double? = null
    }

    class TodayTrips : Serializable {
        @SerializedName("is_completed")
        @Expose
        val isCompleted: Int? = null

        @SerializedName("is_cancelled")
        @Expose
        val isCancelled: Int? = null

        @SerializedName("total_amount")
        @Expose
        val totalAmount: Double? = null

        @SerializedName("amount")
        @Expose
        val amount: Amount? = null
    }

    class YesterdayTrips : Serializable {
        @SerializedName("is_completed")
        @Expose
        val isCompleted: Int? = null

        @SerializedName("is_cancelled")
        @Expose
        val isCancelled: Int? = null

        @SerializedName("total_amount")
        @Expose
        val totalAmount: Double? = null

        @SerializedName("amount")
        @Expose
        val amount: Amount? = null
    }

    class WeeklyTrips : Serializable {
        @SerializedName("is_completed")
        @Expose
        val isCompleted: Int? = null

        @SerializedName("is_cancelled")
        @Expose
        val isCancelled: Int? = null

        @SerializedName("total_amount")
        @Expose
        val totalAmount: Double? = null

        @SerializedName("amount")
        @Expose
        val amount: Amount? = null
    }

    class MonthlyTrips : Serializable {
        @SerializedName("is_completed")
        @Expose
        val isCompleted: Int? = null

        @SerializedName("is_cancelled")
        @Expose
        val isCancelled: Int? = null

        @SerializedName("total_amount")
        @Expose
        val totalAmount: Double? = null

        @SerializedName("amount")
        @Expose
        val amount: Amount? = null
    }

    class TotalTrips : Serializable {
        @SerializedName("is_completed")
        @Expose
        val isCompleted: Int? = null

        @SerializedName("is_cancelled")
        @Expose
        val isCancelled: Int? = null

        @SerializedName("total_amount")
        @Expose
        val totalAmount: Double? = null

        @SerializedName("amount")
        @Expose
        val amount: Amount? = null
    }

    class Report : Serializable {
        @SerializedName("day")
        @Expose
        val day: Day? = null

        @SerializedName("hour")
        @Expose
        val hour: Hour? = null

        @SerializedName("month")
        @Expose
        val month: Month? = null

        @SerializedName("year")
        @Expose
        val year: Year? = null

        class Day : Serializable {
            @SerializedName("horizontal_keys")
            @Expose
            val horizontalKeys: List<String>? = null

            @SerializedName("is_available")
            @Expose
            val isAvailable: Int? = null

            @SerializedName("values")
            @Expose
            val values: ArrayList<String>? = null

        }

        class Hour : Serializable {
            @SerializedName("horizontal_keys")
            @Expose
            val horizontalKeys: List<String>? = null

            @SerializedName("is_available")
            @Expose
            val isAvailable: Int? = null

            @SerializedName("values")
            @Expose
            val values: ArrayList<String>? = null
        }

        class Month : Serializable {
            @SerializedName("horizontal_keys")
            @Expose
            val horizontalKeys: List<String>? = null

            @SerializedName("is_available")
            @Expose
            val isAvailable: Int? = null

            @SerializedName("values")
            @Expose
            val values: ArrayList<String>? = null
        }

        class Year : Serializable {
            @SerializedName("horizontal_keys")
            @Expose
            val horizontalKeys: List<String>? = null

            @SerializedName("is_available")
            @Expose
            val isAvailable: Int? = null

            @SerializedName("values")
            @Expose
            val values: List<String>? = null
        }
    }
}