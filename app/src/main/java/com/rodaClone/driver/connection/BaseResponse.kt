package com.rodaClone.driver.connection

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.rodaClone.driver.connection.responseModels.*
import com.rodaClone.driver.loginSignup.vehicleFrag.model.SignupServiceLocData
import com.rodaClone.driver.loginSignup.vehicleFrag.model.SignupTypeData
import java.io.Serializable


/**
 * Base/Common response objects which will be in all API response
 * are used here
 * */
open class BaseResponse {

    @Expose
    var success: Any? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("notification_enum")
    @Expose
    var notificationEnum: String? = null


    @Expose
    var success_message: String? = null

    @Expose
    var error: Any? = null

    @Expose
    var data: Any? = null

    @SerializedName("token_type")
    @Expose
    var tokenType: String? = null

    @SerializedName("access_token")
    @Expose
    var accessToken: String? = null

    @SerializedName("result")
    @Expose
    val result: RequestDataResult? = null


    @SerializedName("faq")
    @Expose
    val faq: List<Faq>? = null


    /**
     * This method contains all api's data object classes
     */
    class DataObjectsAllApi {
        @SerializedName("types")
        @Expose
        var types: ArrayList<SignupTypeData?>? = null

        @SerializedName("driver")
        @Expose
        val driver: ReqDriverData? = null

//        @SerializedName("document")
//        @Expose
//        val document: List<Document>? = null

        @SerializedName("document")
        @Expose
        var document: ArrayList<GroupDocument> = arrayListOf()

        @SerializedName("head_office_number")
        var headOfficeNumber: HeadOfficeNumber? = HeadOfficeNumber()

        @SerializedName("customer_care_number")
        var customerCareNumber: String? = null

        @SerializedName("complaint")
        @Expose
        val complaint: List<Complaint>? = null

        @SerializedName("sos")
        @Expose
        val sos: List<Sos>? = null

        @SerializedName("result")
        @Expose
        val result: RequestDataResult? = null

        @SerializedName("user")
        @Expose
        val user: User? = null

        /**
         * its for accept reject response
         */
        @SerializedName("data")
        @Expose
        val requestData: RequestResponseData? = null


        @SerializedName("trips")
        @Expose
        val tripsData: MetaData? = null

        @SerializedName("reasons")
        @Expose
        val reasons: List<CancelReason>? = null

        /**
         * its for wallet response
         */

        @SerializedName("total_amount")
        var totalAmount: Double? = null

        @SerializedName("currency")
        var currency: String? = null

        @SerializedName("wallet_transaction")
        var walletTransaction: ArrayList<WalletResponsModel>? = arrayListOf()

        @SerializedName("subscription")
        var subscription: ArrayList<SubscriptionModel> = arrayListOf()

        @SerializedName("currency_symbol")
        var currency_symbol: String? = null

        @SerializedName("profile_pic")
        var profile_pic: String? = null

        @SerializedName("user_name")
        var user_name: String? = null

        @SerializedName("subscription_mode")
        var subscriptionMode: String? = null

        @SerializedName("all_documents_upload")
        val allDocumentsUpload: Boolean? = null

        @SerializedName("referral_code")
        var refCode: String? = null

        @SerializedName("referral_amount")
        var refAmount: String? = null

        @SerializedName("refer_by_driver_amount")
        var referByDriverAmount: String? = null

        @SerializedName("orders")
        var orders: OrderData? = OrderData()
    }


    class RequestDataResult {
        @SerializedName("data")
        @Expose
        val data: RequestResponseData? = null

        @SerializedName("type")
        @Expose
        val type: Int? = null

        @SerializedName("latitude")
        @Expose
        val lattitude: String? = null

        @SerializedName("location_id")
        @Expose
        val locationID: String? = null

        @SerializedName("location_approve")
        @Expose
        val locApprove: String? = null


        @SerializedName("longitude")
        @Expose
        val langitude: String? = null

        @SerializedName("address")
        @Expose
        val address: String? = null

        @SerializedName("upload_status")
        @Expose
        val uploadStatus: Boolean? = null

        @SerializedName("upload_image_url")
        @Expose
        val uploadImageUrl: String? = null

        @SerializedName("retake_image")
        @Expose
        val retake_image: Boolean? = null

    }

    class OnlineOfflineData {
        @SerializedName("online_by")
        @Expose
        var onlineStatus: Int? = null

    }

    class NewUser {


        @SerializedName("new_user")
        @Expose
        var newUser: Boolean? = null

        @SerializedName("client_id")
        @Expose
        var clientId: String? = null

        @SerializedName("client_secret")
        @Expose
        var clientSecret: String? = null

        @SerializedName("error_code")
        @Expose
        var errorCode: Int? = null

    }

    class SignupResponseData {
        @SerializedName("client_id")
        @Expose
        val clientId: String? = null

        @SerializedName("client_secret")
        @Expose
        val clientSecret: String? = null
    }

    class SignupServiceLocation {

        @SerializedName("ServiceLocation")
        @Expose
        val serviceLocation: List<SignupServiceLocData>? = null
    }

    class ReqDriverData : Serializable {
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

        @SerializedName("referral_code")
        @Expose
        val referralCode: String? = null

        @SerializedName("approve_status")
        @Expose
        val approveStatus: Int? = null

        @SerializedName("document_upload_status")
        @Expose
        val documentStatus: Int? = null

        @SerializedName("block_reson")
        @Expose
        val blockReason: String? = null

        @SerializedName("subscription_type")
        @Expose
        val subscriptionType: Int? = null

        @SerializedName("subscription_status")
        @Expose
        val subscriptionStatus: Boolean? = null

        @SerializedName("subscription")
        @Expose
        val subscription: Subscription? = null

        @SerializedName("phone_number")
        @Expose
        val phoneNumber: String? = null

        @SerializedName("type_id")
        @Expose
        val typeId: String? = null

        @SerializedName("today_completed")
        @Expose
        val completed: Int? = null

        @SerializedName("today_cancelled")
        @Expose
        val cancelled: Int? = null

        @SerializedName("wallet_amount")
        @Expose
        val walletAmount: String? = null

        @SerializedName("today_earnings")
        @Expose
        val tdyEarnings: String? = null

        @SerializedName("driver_notes")
        @Expose
        val driver_notes: String? = null


        @SerializedName("profile_pic")
        @Expose
        val profilePic: String? = null

        @SerializedName("country")
        @Expose
        val country: String? = null

        @SerializedName("dial_code")
        @Expose
        val dialCode: String? = null

        @SerializedName("country_code")
        @Expose
        val countryCode: String? = null

        @SerializedName("currency_code")
        @Expose
        val currencyCode: String? = null

        @SerializedName("currency_symbol")
        @Expose
        val currencySymbol: String? = null

        @SerializedName("online")
        @Expose
        val driverStatus: Int? = null

        @SerializedName("meta")
        @Expose
        val metaData: MetaData? = null

        @SerializedName("changed_location_lat")
        @Expose
        val changed_location_lat: Double? = null

        @SerializedName("changed_location_long")
        @Expose
        val changed_location_long: Double? = null

        @SerializedName("changed_location_id")
        @Expose
        val changed_location_id: Int? = null

        @SerializedName("changed_location_adddress")
        @Expose
        val changed_location_adddress: String? = null

        @SerializedName("document_expiry")
        @Expose
        var documentExpiry: ArrayList<DocumentExpiry> = arrayListOf()

        @SerializedName("service_category")
        @Expose
        val serviceCategory: String? = null

    }

    class MetaData {
        @SerializedName("data")
        @Expose
        val data: RequestResponseData? = null
    }


    class RequestBillDataResponse : Serializable {
        @SerializedName("data")
        @Expose
        val billData: RequestResponseDataClass? = null
    }

    class CancelReason {
        @SerializedName("id")
        @Expose
        val id: Int? = null

        @SerializedName("reason")
        @Expose
        val reason: String? = null

        @SerializedName("user_type")
        @Expose
        val userType: String? = null

        @SerializedName("trip_status")
        @Expose
        val tripStatus: String? = null

        @SerializedName("pay_status")
        @Expose
        val payStatus: Int? = null

        @SerializedName("active")
        @Expose
        val active: Int? = null

        @SerializedName("created_at")
        @Expose
        val createdAt: String? = null

        @SerializedName("updated_at")
        @Expose
        val updatedAt: String? = null

        @SerializedName("deleted_at")
        @Expose
        val deletedAt: Any? = null

    }
}

data class User(
    @SerializedName("slug") var slug: String? = null,
    @SerializedName("firstname") var firstname: String? = null,
    @SerializedName("lastname") var lastname: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("phone_number") var phoneNumber: String? = null,
    @SerializedName("currency") var currency: String? = null,
    @SerializedName("country") var country: String? = null,
    @SerializedName("profile_pic") var profilePic: String? = null,
    @SerializedName("car_details") var carDetails: CarDetails? = CarDetails()
)

data class CarDetails(

    @SerializedName("car_number") var carNumber: String? = null,
    @SerializedName("car_model") var carModel: String? = null,
    @SerializedName("car_year") var carYear: String? = null,
    @SerializedName("car_colour") var carColour: String? = null,
    @SerializedName("zone_name") var zoneName: String? = null,
    @SerializedName("vehicle_type") var vehicleType: String? = null

)

data class DocumentExpiry(

    @SerializedName("document_name") var documentName: String? = null,
    @SerializedName("document_images") var documentImages: String? = null

)
data class HeadOfficeNumber(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("value") var value: String? = null,
    @SerializedName("status") var status: Int? = null,
    @SerializedName("slug") var slug: String? = null,
    @SerializedName("type") var type: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("deleted_at") var deletedAt: String? = null,
    @SerializedName("Image") var Image: String? = null

)