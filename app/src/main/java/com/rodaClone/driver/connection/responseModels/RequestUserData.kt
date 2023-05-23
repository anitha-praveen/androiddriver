package com.rodaClone.driver.connection.responseModels

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class RequestUserData : Serializable {
    @SerializedName("id")
    @Expose
    val id: Int? = null

    @SerializedName("slug")
    @Expose
    val slug: String? = null

    @SerializedName("firstname")
    @Expose
    val firstname: String? = null

    @SerializedName("lastname")
    @Expose
    val lastname: String? = null

    @SerializedName("email")
    @Expose
    val email: String? = null

    @SerializedName("phone_number")
    @Expose
    val phoneNumber: String? = null

    @SerializedName("gender")
    @Expose
    val gender: String? = null

    @SerializedName("time_zone")
    @Expose
    val timeZone: String? = null

    @SerializedName("user_type")
    @Expose
    val userType: Any? = null

    @SerializedName("device_info_hash")
    @Expose
    val deviceInfoHash: String? = null

    @SerializedName("application_id")
    @Expose
    val applicationId: Any? = null

    @SerializedName("avatar")
    @Expose
    val avatar: Any? = null

    @SerializedName("active")
    @Expose
    val active: Int? = null

    @SerializedName("last_seen")
    @Expose
    val lastSeen: Any? = null

    @SerializedName("social_unique_id")
    @Expose
    val socialUniqueId: Any? = null

    @SerializedName("mobile_application_type")
    @Expose
    val mobileApplicationType: String? = null

    @SerializedName("token")
    @Expose
    val token: Any? = null

    @SerializedName("country_code")
    @Expose
    val countryCode: String? = null

    @SerializedName("created_at")
    @Expose
    val createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    val updatedAt: String? = null

    @SerializedName("deleted_at")
    @Expose
    val deletedAt: Any? = null

    @SerializedName("profile_pic")
    @Expose
    val profilePic: String? = null

    @SerializedName("referral_code")
    @Expose
    val referralCode: String? = null

    @SerializedName("online_by")
    @Expose
    val onlineBy: Int? = null



}