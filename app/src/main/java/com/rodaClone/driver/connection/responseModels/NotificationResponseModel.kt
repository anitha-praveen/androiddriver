package com.rodaClone.driver.connection.responseModels

import com.google.gson.annotations.SerializedName

data class NotificationResponseModel (
    @SerializedName("current_page")
    var currentPage: Int? = null,
    @SerializedName("data")
    var data: ArrayList<NotificationData>? = null,
    @SerializedName("first_page_url")
    var firstPageUrl: String? = null,
    @SerializedName("from")
    var from: Int? = null,
    @SerializedName("last_page")
    var lastPage: Int? = null,
    @SerializedName("last_page_url")
    var lastPageUrl: String? = null,
    @SerializedName("next_page_url")
    var nextPageUrl: String? = null,
    @SerializedName("path")
    var path: String? = null,
    @SerializedName("per_page")
    var perPage: Int? = null,
    @SerializedName("prev_page_url")
    var prevPageUrl: String? = null,
    @SerializedName("to")
    var to: Int? = null,
    @SerializedName("total")
    var total: Int? = null
    )


    data class NotificationData(


        @SerializedName("id")
        var id: Int? = null,
        @SerializedName("title")
        var title: String? = null,
        @SerializedName("driver_id")
        var driverId: String? = null,
        @SerializedName("user_id")
        var userId: String? = null,
        @SerializedName("sub_title") var subTitle: String? = null,
        @SerializedName("message") var message: String? = null,
        @SerializedName("has_redirect_url") var hasRedirectUrl: String? = null,
        @SerializedName("redirect_url") var redirectUrl: String? = null,
        @SerializedName("image1") var image1: String? = null,
        @SerializedName("image2") var image2: String? = null,
        @SerializedName("image3") var image3: String? = null,
        @SerializedName("slug") var slug: String? = null,
        @SerializedName("date") var date: String? = null,
        @SerializedName("status") var status: Int? = null,
        @SerializedName("created_at") var createdAt: String? = null,
        @SerializedName("updated_at") var updatedAt: String? = null,
        @SerializedName("deleted_at") var deletedAt: String? = null,
        @SerializedName("notification_type") var notificationType: String? = null,
        @SerializedName("images1") var images1: String? = null,
        @SerializedName("images2") var images2: String? = null,
        @SerializedName("images3") var images3: String? = null


    )