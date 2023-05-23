package com.rodaClone.driver.ut

import android.Manifest
import android.os.Build

class Config {
    companion object {


        val CLOSE_ACCEPTREJECT: String = "close_accept_reject"
        val status: String = "status"
        val DRIVER_DATA: String = "DRIVER_DATA"
        val loc_id: String = "loc_id"
        val loc_approve: String = "loc_approve"
        val LocationId: String = "location_id"
        const val address: String = "address"

        /**
         * Common values
         */


        val CodeValue: String = "V-b6ea1128db9cf21e2808dfc1d2f02ae5" /* for development 1.11 */
        val sharedPrefName = "secret_shared_prefs_dy_driver_pro"
        var Array_permissions = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        var storagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_MEDIA_IMAGES
            )
        } else {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }


        var WAKE_LOCK_TAG = "myapp:LIMO_WAKE_LOCK_TAG"
        var WAKE_LOCK_TAG2 = "myapp:LIMO_WAKE_LOCK_TAG2"

        /**
         * Permission codes
         */
        const val NORMAL_LOCATION_PERMISSIONS = 9000
        const val BACKGROUND_LOCATION_PERMISSION = 9001
        const val APP_UPDATE = 123

        /**
         * Api URLS
         */



        const val BASEURL = "http://3.216.234.12/roda/public/api/V1/" /*development url*/

        const val SOCKET_URL = "http://3.216.234.12:5001"

        const val GooglBaseURL = "https://maps.googleapis.com/"
        const val DirectionURL = "maps/api/directions/json?"
        const val LANGUAGE_CODE_API = "languages"
        const val SIGN_IN_API = "driver/signin"
        const val TYPES_LIST_API = "types/list"
        const val SERVICE_LOCATION_LIST = "servicelocation/list"
        const val SIGN_UP_API = "driver/signup"
        const val AUTH_TOKEN_API = "auth/token"
        const val DRIVER_REQ_PROGRESS = "driver/request_in_progress"
        const val DRIVER_PROFILE = "driver/profile"
        const val CHECK_PHONE_NUMBER_AVAILABLE = "driver/check/phonenumber"
        const val GET_DOC_LIST = "document"
        const val UPLOAD_DOCUMENT = "driver/document-upload"
        const val GET_COMPLAINTS_LIST = "complaints/list"
        const val POST_COMPLAINT = "complaints/add"
        const val GET_FAQ = "faq"
        const val GET_SOS = "sos"
        const val ONLINE_OFFLINE = "driver/online-update"
        const val REQUEST_RESPOND = "request/respond"
        const val TRIP_ARRIVED = "request/arrive"
        const val TRIP_START = "request/start"
        const val TRIP_END = "request/end"
        const val CANCELATION_LIST = "cancellation/list"
        const val UPDATE_CANCEL = "request/cancel/driver"
        const val HISTORY_URL = "request/user/trip/history"
        const val RATE = "request/rating"
        const val SAVE_SOS = "sos/store"
        const val DELETE_SOS = "sos/delete/{id}"
        const val LOG_OUT_URL = "user/logout"
        const val WALLET_LIST_URL = "wallet"
        const val WALLET_ADD_AMOUNT = "wallet/add-amount"
        const val GET_DASH_DETAILS = "dashboard"
        const val DISPUTE = "complaints/trip-list"
        const val GET_SUBSCRIPTION_LIST = "subscription/list"
        const val SUBSCRIPTION_ADD = "subscription/add"
        const val LOCATION_APPROVE = "request/approve-change-location"

        const val NOTIFIACTION_URL = "notification/list"

        const val CREATE_REQUEST = "request/create"
        const val GET_COMPANY_LIST = "company/user"
        const val GET_VEHICLE_MODELS = "get/model"
        const val CUSTOM_OTP_URL = "user/sendotp"
        const val SELECTED_LANGUAGE = "user/profile/language"
        const val GET_REFERAL_API = "get/referral"
        const val SINGNLE_HISTORY = "request/single-history"
        const val NIGHT_IMAGE_UPLOAD = "request/image-upload"
        const val SKIP_NIGHT_IMG = "request/skip-upload"
        const val RETAKE_NIGHT = "request/retake-image"
        const val NIGHT_IMAGE_BOTH = "instant/image/upload"
        const val GROUP_DOCUMENT = "document/document-group"
        const val ORDERID_API = "create/order"

        /**
         * Network parameters
         */
        const val Code = "code"
        const val country = "country"
        const val phone_number = "phone_number"
        const val country_code = "country_code"
        const val country_code_id = "country_code_id"
        const val mode = "mode"
        const val otp = "otp"
        const val device_info_hash = "device_info_hash"
        const val is_primary = "is_primary"
        const val device_type = "device_type"
        const val email = "email"
        const val lastname = "lastname"
        const val firstname = "firstname"
        const val vehicle_type_slug = "vehicle_type_slug"
        const val type_id = "type_id"
        const val car_number = "car_number"
        const val service_location = "service_location"
        const val grant_type = "grant_type"
        const val client_id = "client_id"
        const val client_secret = "client_secret"
        const val document_id = "document_id"
        const val expiry_date = "expiry_date"
        const val issue_date = "issue_date"
        const val document_image = "document_image"
        const val complaint_id = "complaint_id"
        const val answer = "answer"
        const val start_connect = "start_connect"
        const val id = "id"
        const val DRIVERREQUEST = "request_"
        const val REQ_DATA = "reqData"
        const val request_id = "request_id"
        const val DRIVER_LAT = "driver_latitude"
        const val DRIVER_LNG = "driver_longitude"
        const val pick_lat = "pick_lat"
        const val pick_lng = "pick_lng"
        const val drop_lat = "drop_lat"
        const val drop_lng = "drop_lng"
        const val DISTANCE = "distance"
        const val drop_address = "drop_address"
        const val updatedLat = "updatedLat"
        const val updatedLng = "updatedLng"
        const val dropaddresspressed = "dropaddresspressed"
        const val reason: String = "reason"
        const val driver_location: String = "driver_location"
        const val ride_type = "ride_type"
        const val trip_type = "trip_type"
        const val rating = "rating"
        const val feedback = "feedback"
        const val title = "title"
        const val description = "description"
        const val locationchanged_ = "locationchanged_"
        const val waiting_time = "waiting_time"
        const val amount = "amount"
        const val currency: String = "currency"
        const val purpose = "purpose"
        const val lat = "lat"
        const val lng = "lng"
        const val latitude = "latitude"
        const val longitude = "longitude"
        const val request_otp = "request_otp"
        const val referral_code = "referral_code"
        const val payment_opt = "payment_opt"
        const val slug = "slug"
        const val login_method = "login_method"
        const val vehicle_model_name = "vehicle_model_name"
        const val vehicle_model_slug = "vehicle_model_slug"
        const val service_category = "service_category"
        const val brand_label = "brand_label"
        const val company_name = "company_name"
        const val company_no_of_vehicles = "company_no_of_vehicles"
        const val company_phone = "company_phone"
        const val company_slug = "company_slug"
        const val identifier = "identifier"
        const val language = "language"
        const val trip_image = "trip_image"
        const val start_km = "start_km"
        const val end_km = "end_km"
        const val poly_string = "poly_string"
        const val package_changed_ = "package_changed_"
        const val images = "images"
        const val skip = "skip"
        const val photo_upload_ = "photo_upload_"
        const val upload_status = "upload_status"
        const val user_instant_image = "user_instant_image"
        const val driver_instant_image = "driver_instant_image"
        const val trip_end_time = "trip_end_time"
        const val retake = "retake"

        /**
         * Serialization parameters
         */
        const val requestData = "requestData"
        const val isArrivedStatus = "isArrivedStatus"
        const val isPickup = "isPickup"
        const val pickupAddress = "pickupAddress"
        const val APPROVE_STATUS: String = "APPROVE_STATUS"
        const val DOCS_STATUS: String = "DOCS_STATUS"
        const val BLOCK_REASON = "BLOCK_REASON"
        const val SHOW_SUBSCRIBE = "SHOW_SUBSCRIBE"
        const val BALANCE_SUBSCRIPTION_DAYS = "BALANCE_SUBSCRIPTION_DAYS"
        const val EXPIRED_DOCUMENTS = "EXPIRED_DOCUMENTS"
        const val BUBBLE_STATE = "BUBBLE_STATE"
        const val PASS_URI = "PASS_URI"
        const val CUSTOMER_CARE_NUM = "CUSTOMER_CARE_NUM"
        const val HELPLINE_NUM = "HELPLINE_NUM"

        /**
         * Event names
         */
        const val RECEIVE_COUNTRY = "RECEIVE_COUNTRY"
        const val NOTIFY_DOC_UPLOADED = "NOTIFY_DOC_UPLOADED"
        const val RECEIVE_DIRECTION_INVOICE = "RECEIVE_DIRECTION_INVOICE"
        const val RECEIVE_CLOSE_TRIP = "RECEIVE_CLOSE_TRIP"
        const val RECEIVE_TRIP_ADDRESS_CHANGE = "RECEIVE_TRIP_ADDRESS_CHANGE"
        const val RECEIVE_TRIP_OTP = "RECEIVE_TRIP_OTP"
        const val RECEIVE_NO_ITEM_FOUND = "RECEIVE_NO_ITEM_FOUND"
        const val RECEIVE_SUBSCRIPTION_DETAILS = "RECEIVE_SUBSCRIPTION_DETAILS"
        const val RECEIVE_TRIP_IMAGE = "RECEIVE_TRIP_IMAGE"
        const val RECEIVE_BUBBLE_STATE = "RECEIVE_BUBBLE_STATE"
        const val CALL_REQIN_PRO_APPSTATUS = "CALL_REQIN_PRO_APPSTATUS"
        const val RECEIVE_DISTANCE = "RECEIVE_DISTANCE"
        const val LOGOUT_RECEIVER = "LOGOUT_RECEIVER"
        const val RECEIVE_TRIP_DETAILS = "RECEIVE_TRIP_DETAILS"
        const val LOC_TO_RENT_PUSH = "LOC_TO_RENT_PUSH"
        const val RECEIVE_CLEAR_DIST = "RECEIVE_CLEAR_DIST"
        const val RECEIVE_PROFILE_IMAGE = "RECEIVE_PROFILE_IMAGE"
        const val RECEIVE_CURR_LOC_CLICK = "RECEIVE_CURR_LOC_CLICK"
        const val NOTIFY_DRAWER_IMAGE_CHANGED = "NOTIFY_DRAWER_IMAGE_CHANGED"
        const val NEW_REQUEST = "NEW_REQUEST"

        //device manufacturer
        const val REAL_ME_DEVICE_NAME = "Realme"
        const val USER_NIGHT_PHOTO = "USER_NIGHT_PHOTO"
        const val PUSH_UPLOAD_NIGHT = "PUSH_UPLOAD_NIGHT"

        //bundle keys
        const val groupDocument_b = "groupDocuments"
    }
}
