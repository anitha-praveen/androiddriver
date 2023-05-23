package com.rodaClone.driver.drawer.trip

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.fragment.app.FragmentActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseVM
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.connection.ConnectionHelper
import com.rodaClone.driver.connection.CustomException
import com.rodaClone.driver.connection.responseModels.NightUploadBoth
import com.rodaClone.driver.connection.responseModels.RequestResponseData
import com.rodaClone.driver.connection.responseModels.Route
import com.rodaClone.driver.ut.*
import com.rodaClone.driver.ut.Utilz.isGoogleMapsInstalled
import kotlinx.coroutines.*
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.net.URL
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class TripFragmentVM @Inject constructor(
    val application: Application,
    val session: SessionMaintainence,
    val mConnect: ConnectionHelper,
    private val mapsHelper: MapsHelper
) :
    BaseVM<BaseResponse, TripNavigator>(session, mConnect) {
    var waitingTime = ObservableField("0.0") /* Just to show waiting time in ui as String */
    var userName = ObservableField<String>()
    var phoneNumber = ObservableField<String>()
    var reqID = ObservableField<String>()
    var profileImage = ObservableField("")
    var reqNumber = ObservableField("")

    var realPath: String? = null
    var realFile: File? = null
    var genericsType: GenericsType? = null
    var bitmapProfilePicture: ObservableField<GenericsType> = ObservableField<GenericsType>()

    /*
    donot use currentAddress in xml since it is updated form coroutine io scope
     */
    var currentAddress = ObservableField<String>("")
    var currentLatlng = ObservableField<LatLng>()
    var pickupLatlng = ObservableField<LatLng>()
    var dropLatlng = ObservableField<LatLng>()
    var stopLatLng = ObservableField<LatLng>()
    var changdeDropLatlng = ObservableField<LatLng>()
    var changedDropAddress = ObservableField<String>("")
    var changedLocId = ObservableField<String>("")
    var userRatingString = ObservableField("")
    var tripPaymentMode = ObservableField<String>()
    var isArrived = ObservableBoolean()
    var isCancelled = ObservableBoolean()
    var isCompleted = ObservableBoolean()
    var isTripStarted = ObservableBoolean()
    var dropCheck = ObservableBoolean(true)
    var mapType = ObservableBoolean(false)
    var isPipEnabled = ObservableBoolean(false)
    var isInstantJob = ObservableBoolean(false)
    var routeDest: Route? = null
    var pointsDest: MutableList<LatLng>? = null
    var lineOptionsDest1: PolylineOptions? = null
    var lineOptionDesDark: PolylineOptions? = null
    var polyLineDest1: Polyline? = null
    var polyLineDestDark: Polyline? = null
    var map: HashMap<String, String> = HashMap()
    var fusedLocationClient: FusedLocationProviderClient? = null
    var googleMap: GoogleMap? = null
    private var driverMarker: Marker? = null
    var pickupMarker: Marker? = null
    var dropMarker: Marker? = null
    var stopMarker: Marker? = null
    lateinit var requestData: RequestResponseData
    lateinit var driverRequestData: BaseResponse.ReqDriverData
    var apiCallCount: Int = 0
    var mLocationRequest: LocationRequest? = null
    lateinit var allDataObjects: BaseResponse.DataObjectsAllApi
    var seconds = 0   /* This is the total waiting time and it is initialized at onViewCreate. */
    var graceArriveLimit =
        0 /* This is grace time limit before start given from backend and it is initialized when arrived api called*/
    var graceStartLimit =
        0 /* This is grace time limit after start given from backend and it is initialized when start api called*/
    var graceArriveTime =
        0 /* This is the grace time used before start trip and it is initialized at onViewCreate. */
    var graceStartTime =
        0 /* This is the grace time used after start trip and it is initialized at onViewCreate. */
    var totalDistanceTravelled: Double =
        0.0 /* This is the total distance travelled by the driver after the trip started. */
    var tripDistance = ObservableField("0")

    var stratKM = ObservableField("")

    var userImageUrl = ObservableField("")

    val showNotes = ObservableBoolean(false)
    val userNotes = ObservableField("")

    val showAddress = ObservableBoolean(true)


    val mainHandler = Handler(Looper.getMainLooper())
    var isDispatch = ObservableBoolean(false)

    //not registerd user instant trip
    val showProfile = ObservableBoolean(false)
    var requestRef: DatabaseReference? = null
    var driverRef: DatabaseReference? = null

    var isOutStationTrip = ObservableBoolean(false)
    var isOutsationOrRental = ObservableBoolean(false)
    val tripAddress = ObservableField("")

    var tripOtp = "" /* To start trip */
    val isRentalTrip = ObservableBoolean(false)
    var isDriverPicUpload = false /* holds the value for if photo of driver uploaded or not */
    var isUserPicUpload =
        ObservableBoolean(false) /* holds the value for if photo of user uploaded or not */
    var isNightPhotoSkip = false /* holds the value for if photo of user upload skipped by driver */

    init {
        var database = FirebaseDatabase.getInstance()

        requestRef = database.getReference("requests")
        driverRef = database.getReference("drivers")
    }


    /* UI Click block */

    fun onSosClick() {
        getNavigator().openSos()
    }

    fun onMenuClick() {
        getNavigator().openSideMenu()
    }

    fun onCallClick(view: View) {
        if (phoneNumber.get() != null) {
            val callIntent = Intent(Intent.ACTION_DIAL)
            callIntent.data = Uri.parse("tel:" + phoneNumber.get()!!.trim())
            view.context.startActivity(callIntent)
        } else {
            if (requestData.instant_phone_number != null) {
                val callIntent = Intent(Intent.ACTION_DIAL)
                callIntent.data = Uri.parse("tel:" + requestData.instant_phone_number)
                view.context.startActivity(callIntent)
            }

        }
    }

    fun onClickDriverArrive() {
        if (getNavigator().isNetworkConnected()) {
            arriveApi()
        } else getNavigator().showNetworkUnAvailable()

//        if (isDispatch.get() || !shouldITakePhoto()) {
//            if (isLoading.value == false) {
//                if (getNavigator().isNetworkConnected()) {
//                    arriveApi()
//                } else getNavigator().showNetworkUnAvailable()
//            }
//        } else if (shouldITakePhoto()) {
//            flagNightPhoto = 1
//            if (isDriverPicUpload && isUserPicUpload.get())
//                arriveApi()
//            else if (!isDriverPicUpload)
//                getNavigator().showTakePhotoDialog()
//            else if (!isUserPicUpload.get())
//                getNavigator().showSkipOrReject(true)
//        }
    }

    fun onClickNavigation(view: View) {
        /* open google map for navigation */
        if (isTripStarted.get()) {
            if (dropLatlng.get() != null && dropLatlng.get()!!.latitude != 0.0 && dropLatlng.get()!!.longitude != 0.0)
                openMap(dropLatlng.get()!!.latitude, dropLatlng.get()!!.longitude, view.context)
        } else {
            if (pickupLatlng.get() != null && pickupLatlng.get()!!.latitude != 0.0 && pickupLatlng.get()!!.longitude != 0.0)
                openMap(pickupLatlng.get()!!.latitude, pickupLatlng.get()!!.longitude, view.context)
        }
    }

    fun onclickChangeTripStatus() {
        /* Start trip and End trip API calls */
        if (isLoading.value == false) {
            if (!isTripStarted.get()) {
                if (!shouldITakePhoto()) {
                    getNavigator().openOtpDialog()
                } else if (shouldITakePhoto()) {
                    if (isNightPhotoSkip) {
                        getNavigator().openOtpDialog()
                    } else if (isDriverPicUpload && isUserPicUpload.get()) {
                        getNavigator().openOtpDialog()
                    } else if (!isDriverPicUpload) {
                        if(isDispatch.get()){
                            flagNightPhoto = 2
                            getNavigator().uploadNightImageBoth()
                        }else{
                            flagNightPhoto = 2
                            getNavigator().showTakePhotoDialog(false)
                        }
                    } else if (!isUserPicUpload.get()) {
                        flagNightPhoto = 2
                        getNavigator().showSkipOrReject(true)
                    }
                }
            } else {
                getNavigator().showConfirmEndTripAlert()
            }
        }
    }

    fun onClickSkipPhoto() {
        getNavigator().showSkipOrReject(false)
        skipNightImgApiCall()
    }

    fun onClickReject() {
        getNavigator().showSkipOrReject(false)
        getNavigator().openCancelReasons()
    }

    fun onClickAcceptNightPhoto() {
        getNavigator().showSkipOrReject(false)
        when (flagNightPhoto) {
            1 -> arriveApi()
            2 -> {
                if (!isInstantJob.get())
                    getNavigator().openOtpDialog()
                else
                    startTrip()
            }
        }
    }

    fun onclickRetakeNightPhoto() {
        retakeApiCall()
    }

    fun onClickUserNight() {
        if (userSelfie.get()!!.isNotEmpty())
            getNavigator().showImage(
                translationModel?.txt_customer ?: "",
                userSelfie.get()!!,
                false
            )
    }

    /* API calls */
    private fun arriveApi() {
        apiCallCount = 1
        map.clear()
        map[Config.request_id] = reqID.get()!!
        map[Config.DRIVER_LAT] = currentLatlng.get()!!.latitude.toString()
        map[Config.DRIVER_LNG] = currentLatlng.get()!!.longitude.toString()
        arriveApi(map)
    }

    fun startTrip() {
        apiCallCount = 2
        if (getNavigator().isNetworkConnected()) {
            if (validationForStartTrip()) {
                requestbody.clear()
                requestbody[Config.request_id] = reqID.get()!!.toRequestBody("text/plain".toMediaTypeOrNull())
                requestbody[Config.pick_lat] =  currentLatlng.get()!!.latitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                requestbody[Config.pick_lng] = currentLatlng.get()!!.longitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                requestbody[Config.request_otp] =
                     tripOtp.toRequestBody("text/plain".toMediaTypeOrNull())

                if (isOutStationTrip.get() || isRentalTrip.get()) {
                    requestbody[Config.start_km] =
                        stratKM.get()?.toRequestBody("text/plain".toMediaTypeOrNull()) ?: "".toRequestBody("text/plain".toMediaTypeOrNull())
                    val reqFile =  tripStartImage?.asRequestBody("image/*".toMediaTypeOrNull())
                    body = reqFile?.let {
                        MultipartBody.Part.createFormData(
                            Config.trip_image,
                            tripStartImage!!.name,
                            it
                        )
                    }
                    startWIthImageApi()
                } else
                    startTripBase()
            }
        } else
            getNavigator().showNetworkUnAvailable()
    }

    fun callEndTripApi() {
        apiCallCount = 3
        if (getNavigator().isNetworkConnected()) {
            if (currentAddress.get()!!.isNotEmpty()) {
                requestbody.clear()
                requestbody[Config.request_id] =  reqID.get()!!.toRequestBody("text/plain".toMediaTypeOrNull())
                requestbody[Config.drop_lat] = currentLatlng.get()!!.latitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                requestbody[Config.drop_lng] =

                    currentLatlng.get()!!.longitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                requestbody[Config.drop_address] =
                     currentAddress.get()!!.toRequestBody("text/plain".toMediaTypeOrNull())

                requestbody[Config.waiting_time] =

                         waitingTimeInMinutes.toRequestBody("text/plain".toMediaTypeOrNull())

                requestbody[Config.DISTANCE] =
                    tripDistance.get()!!.toRequestBody("text/plain".toMediaTypeOrNull())

                if (isOutstationTwoWay) {
                    requestbody[Config.trip_end_time] =
                         getEndTime().toRequestBody("text/plain".toMediaTypeOrNull())
                }
                if (isOutStationTrip.get() || isRentalTrip.get()) {
                    requestbody[Config.end_km] = endKM.toRequestBody("text/plain".toMediaTypeOrNull())

                    val reqFile = tripEndImage?.asRequestBody("image/*".toMediaTypeOrNull())
                    body = reqFile?.let {
                        MultipartBody.Part.createFormData(
                            Config.trip_image,
                            tripEndImage!!.name,
                            it
                        )
                    }
                    endTripWithImageBase()
                } else endTripBase()
            }
        } else
            getNavigator().showNetworkUnAvailable()
    }

    fun approveLocation(stringExtra: String, loc_id: String, status: Int, polyString: String) {
        if (getNavigator().isNetworkConnected()) {
            apiCallCount = 4
            map.clear()
            map[Config.request_id] = reqID.get()!!
            map[Config.latitude] = currentLatlng.get()!!.latitude.toString()
            map[Config.longitude] = currentLatlng.get()!!.longitude.toString()
            map[Config.address] = stringExtra
            map[Config.LocationId] = loc_id
            map[Config.status] = "" + status
            if (polyString.isNotEmpty())
                map[Config.poly_string] = polyString
            locationApprove(map)
        } else
            getNavigator().showNetworkUnAvailable()
    }

    private fun uploadImage() {
        /* Upload image of driver for night time
        * This method is called from individual driver image upload*/
        apiCallCount = 5
        requestbody.clear()

        requestbody[Config.request_id] =
            reqID.get()?.toRequestBody("text/plain".toMediaTypeOrNull())!!
        if (realPath != null) {
            val reqFile =  realFile?.asRequestBody("image/*".toMediaTypeOrNull())
            body =
                reqFile?.let {
                    MultipartBody.Part.createFormData(Config.images, realFile!!.name,
                        it
                    )
                }
        }
        uploadNightImageBase()
    }

    fun skipNightImgApiCall() {
        /* Skip user night image upload */
        if (getNavigator().isNetworkConnected()) {
            apiCallCount = 6
            map.clear()
            map[Config.request_id] = reqID.get()!!
            map[Config.skip] = "1"
            skipNightImgBase(map)
        } else {
            getNavigator().showNetworkUnAvailable()
        }
    }

    fun retakeApiCall() {
        /* request  user night image re-upload */
        if (getNavigator().isNetworkConnected()) {
            apiCallCount = 7
            map.clear()
            map[Config.request_id] = reqID.get()!!
            requestRetakeBase(map)
        } else {
            getNavigator().showNetworkUnAvailable()
        }
    }

    var userBody: MultipartBody.Part? = null
    var driverBody: MultipartBody.Part? = null
    fun uploadUserDriver() {
        if (getNavigator().isNetworkConnected()) {
            apiCallCount = 8

            requestbody.clear()

            requestbody[Config.request_id] =
                reqID.get()?.toRequestBody(("text/plain".toMediaTypeOrNull()))!!

            if (userPath != null) {
                val reqFile = userFile!!.asRequestBody("image/*".toMediaTypeOrNull())
                userBody = MultipartBody.Part.createFormData(
                    Config.user_instant_image,
                    userFile!!.name,
                    reqFile
                )
            }

            if (driverPath != null) {
                val reqFile =  driverFile!!.asRequestBody("image/*".toMediaTypeOrNull())
                driverBody = MultipartBody.Part.createFormData(
                    Config.driver_instant_image,
                    driverFile!!.name,
                    reqFile
                )
            }

            driverBody?.let { userBody?.let { user -> uploadNightBothBase(it, user) } }
        }
    }

    fun uploadDriver() {
        /*
        This method is called when scenario driver have to upload both images (driver and customer)
        but skipped user image and uploaded only driver image
         */
        if (getNavigator().isNetworkConnected()) {
            apiCallCount = 9
            requestbody.clear()
            requestbody[Config.request_id] =
                reqID.get()?.let {  it.toRequestBody("text/plain".toMediaTypeOrNull()) }!!
            if (driverPath != null) {
                val reqFile =  driverFile?.asRequestBody("image/*".toMediaTypeOrNull())
                body = reqFile?.let {
                    MultipartBody.Part.createFormData(Config.images, driverFile!!.name,
                        it
                    )
                }
                uploadNightImageBase()
            }
        } else
            getNavigator().showNetworkUnAvailable()
    }

    var tripStartTime = ""
    var flagNightPhoto = 0 /* use to know which api needs to be called after night upload photo
    1 -> arrive
    2 -> start trip
    3 -> end trip
    */
    override fun onSuccessfulApi(response: BaseResponse?) {
        isLoading.value = false
        when (apiCallCount) {
            1 -> {
                if (response?.data != null)
                    allDataObjects = Gson().fromJson(
                        Gson().toJson(response.data),
                        BaseResponse.DataObjectsAllApi::class.java
                    )
                /* arrived api result */
                isArrived.set(true)
                session.clearTripDetails()
                allDataObjects.requestData?.graceWaitingTime?.let {
                    graceArriveLimit = it.toInt() * 60
                }
                allDataObjects.requestData?.driverUploadImage?.let { isDriverPicUpload = it }
                allDataObjects.requestData?.userUploadImage?.let { isUserPicUpload.set(it) }
                allDataObjects.requestData?.skipNightPhoto?.let { isNightPhotoSkip = it }
                session.saveString(SessionMaintainence.DRIVER_TO_PICKUP_POLY, "")
                handleRoutes()
                Utilz.updateDriverTripStatus(1, session.getString(SessionMaintainence.ReqID)!!)
            }
            2 -> {
                /* start api result */
                if (response?.data != null)
                    allDataObjects = Gson().fromJson(
                        Gson().toJson(response.data),
                        BaseResponse.DataObjectsAllApi::class.java
                    )
                session.clearTripDetails()
                session.saveString(SessionMaintainence.IS_TRIP_STARTED, "TRUE")
                session.saveBoolean(SessionMaintainence.IS_GRACE_TIME_COMPLETED, true)
                isTripStarted.set(true)
                allDataObjects.requestData?.graceWaitingTimeAfterStart?.let {
                    graceStartLimit = it.toInt() * 60
                }
                allDataObjects.requestData?.tripStartTime?.let { tripStartTime = it }
                allDataObjects.requestData?.serviceCategory?.let { tripType = it }
                allDataObjects.requestData?.driverUploadImage?.let { isDriverPicUpload = it }
                allDataObjects.requestData?.userUploadImage?.let { isUserPicUpload.set(it) }
                allDataObjects.requestData?.skipNightPhoto?.let { isNightPhotoSkip = it }
                if (tripType.equals("RENTAL", true)) {
                    tripTimeHandler.removeCallbacks(tripTimeRunner)
                    setTimer()
                    showGoogleMapNavButton.set(false)
                    showAddress.set(false)
                }
                allDataObjects.requestData?.dropAddress.let { tripAddress.set(it) }
                totalDistanceTravelled = 0.0
                Utilz.updateDriverTripStatus(2, session.getString(SessionMaintainence.ReqID)!!)
            }
            3 -> {
                /* end trip api result */
                if (response?.data != null)
                    allDataObjects = Gson().fromJson(
                        Gson().toJson(response.data),
                        BaseResponse.DataObjectsAllApi::class.java
                    )
                Utilz.updateDriverTripStatus(3, session.getString(SessionMaintainence.ReqID)!!)
                session.clearTripDetails()
                clearReqId()
                isCompleted.set(true)
                allDataObjects = Gson().fromJson(
                    Gson().toJson(response?.data),
                    BaseResponse.DataObjectsAllApi::class.java
                )
                val intent = Intent(Config.RECEIVE_CLEAR_DIST)
                getNavigator().getCtx()
                    ?.let { LocalBroadcastManager.getInstance(it).sendBroadcast(intent) }
                /*
                Resetting Grace time and Waiting time values when trip completed
                 */
                tripTimeHandler.removeCallbacks(tripTimeRunner)
                mainHandler.removeCallbacks(updateFRB)
                isUpdatingDistanceAndTime = false
                removeWaitingTimeCallBacks()
                removeLocUpdate()
                clearTimeAndDistanceAfterTripCompleted()
                getNavigator().loadBill(allDataObjects.requestData!!)
            }
            4 -> {
                /* drop address change accept reject api result */
                getNavigator().loadRequestProgress()
            }
            5 -> {
                response?.message?.let { getNavigator().showMessage(it) }
                val data = Gson().fromJson(
                    Gson().toJson(response?.data),
                    RequestResponseData::class.java
                )
                data?.userUploadImage?.let { isUserPicUpload.set(it) }
                data?.driverUploadImage?.let { isDriverPicUpload = it }
                getNavigator().showSkipOrReject(true)
            }
            6 -> {
                isNightPhotoSkip = true
                if (isDriverPicUpload) {
                    when (flagNightPhoto) {
                        1 -> arriveApi()
                        2 -> {
                            if (!isInstantJob.get())
                                getNavigator().openOtpDialog()
                        }
                    }
                }
            }
            7 -> {
                isUserPicUpload.set(false)
            }
            8 -> {
                if (response?.data != null) {
                    val result =
                        Gson().fromJson(Gson().toJson(response.data), NightUploadBoth::class.java)
                    result.instantImageUpload?.driverUploadImage?.let { isDriverPicUpload = it }
                    result.instantImageUpload?.userUploadImage?.let { isUserPicUpload.set(it) }
                }
                getNavigator().closeUserDriverDialog()
                if (!isInstantJob.get())
                    getNavigator().openOtpDialog()
            }
            9 -> {
                response?.message?.let { getNavigator().showMessage(it) }
                val data = Gson().fromJson(
                    Gson().toJson(response?.data),
                    RequestResponseData::class.java
                )
                data?.driverUploadImage?.let { isDriverPicUpload = it }
                if (isDriverPicUpload) {
                    getNavigator().closeUserDriverDialog()
                   if(!isInstantJob.get())
                       getNavigator().openOtpDialog()
                }
            }
        }

    }

    override fun onFailureApi(call: Call<BaseResponse?>?, t: CustomException?) {
        isLoading.value = false
        getNavigator().showMessage(t!!.exception!!)
    }

    fun requestLocationUpdates(requireActivity: FragmentActivity) {
        mLocationRequest = LocationRequest.create().apply {
            interval = 1000
            fastestInterval = 1000
            isWaitForAccurateLocation = true
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            maxWaitTime = 100
        }
        if (ContextCompat.checkSelfPermission(
                requireActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient!!.requestLocationUpdates(
                mLocationRequest!!,
                mLocationCallback,
                Looper.myLooper()!!
            )
        }
    }

    var isUpdatingDistanceAndTime = false

    /**
     * Get location on frequent
     * it is same as onLocationChanged.
     * In some situations when app in background mLocationCallback is not triggered so handleLocationWhenAppInBackground is used
     */
    private var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                if (location != null) {
                    /*if (location.isMock) {
                        getNavigator().showMessage("you are using mock location")
                    } else {*/
//                        Log.e(TripFragment.TAG, "mLocationCallback")
                    currentLatlng.set(LatLng(location.latitude, location.longitude))

                    /* Car Move block */
                    animateMarker(
                        LatLng(
                            location.latitude, location.longitude
                        ), location
                    )
                    /* Waiting time block */
                    if (!tripType.equals("RENTAL", true))
                        handleWaitingTime(location)

                    if (!isUpdatingDistanceAndTime && isArrived.get()) {
                        isUpdatingDistanceAndTime = true
                        mainHandler.post(updateFRB)
                    }

                }
                // }
            }
        }
    }

    fun getImageFromS3(profilePic: String?, from: Int) {
        val viewModelJob = Job()
        val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

        uiScope.launch {
            withContext(Dispatchers.IO) {
                val cal = Calendar.getInstance()
                cal.time = Date()
                cal.add(Calendar.HOUR, +1)
                val oneHourLater: Date = cal.time
                val s3: AmazonS3 = AmazonS3Client(
                    BasicAWSCredentials(
                        session.getString(SessionMaintainence.AWS_ACCESS_KEY_ID),
                        session.getString(SessionMaintainence.AWS_SECRET_ACCESS_KEY)
                    )
                )
                s3.setRegion(Region.getRegion(session.getString(SessionMaintainence.AWS_DEFAULT_REGION)))
                val url: URL = s3.generatePresignedUrl(
                    session.getString(SessionMaintainence.AWS_BUCKET),
                    profilePic,
                    oneHourLater
                )
                //setImage(url.toString())
                //vm.refURL.set(url.toString())
                withContext(Dispatchers.Main) {
                    if (from == 0)
                        profileImage.set(url.toString())
                    else getNavigator().loadProfile(url.toString())
                }
            }
        }

    }


    var tripStartImage: File? = null
    var tripStartImagePath: String? = null
    private fun validationForStartTrip(): Boolean {
        var msg = ""
        if (!isDispatch.get() && tripOtp.isEmpty())
            if (!isInstantJob.get())
                msg = "Please enter OTP to start"
        else if (isOutStationTrip.get() && tripStartImagePath.isNullOrEmpty())
            msg = "Upload meter image"
        return if (msg.isNotEmpty()) {
            getNavigator().showMessage(msg)
            false
        } else
            true

    }

    var driverSelfie = ObservableField("")
    var userSelfie = ObservableField("")
    var userPath: String? = null
    var userFile: File? = null
    var driverPath: String? = null
    var driverFile: File? = null
    fun onCaptureImageResult(data: Intent?) {
        if (data != null) {
            /*
            camera flag
             1-> for driver image
             2-> for driverImage for dispatcher or instant trip
             3-> for userImage for dispatcher or instant trip
             */


            if (data.hasExtra("data")) {
                when (cameraFlag) {
                    1 -> {
                        realPath = getNavigator().getCtx()?.let {
                            Utilz.getImageUri(
                                it, data.extras?.get(
                                    "data"
                                ) as Bitmap
                            )
                        }
                        realFile = File(realPath)
                        driverSelfie.set(realPath)
                        genericsType = GenericsType()
                        genericsType!!.set(realFile)
                        bitmapProfilePicture.set(genericsType)
                        uploadImage()
                    }
                    2 -> {
                        driverPath = getNavigator().getCtx()?.let {
                            Utilz.getImageUri(
                                it, data.extras?.get(
                                    "data"
                                ) as Bitmap
                            )
                        }
                        driverPath?.let {
                            driverFile = File(it)
                            getNavigator().setDriver(it)
                        }
                    }
                    3 -> {
                        userPath = getNavigator().getCtx()?.let {
                            Utilz.getImageUri(
                                it, data.extras?.get(
                                    "data"
                                ) as Bitmap
                            )
                        }

                        userPath?.let {
                            userFile = File(it)
                            getNavigator().setUser(it)
                        }
                    }
                }

            }
        }

    }


    var endKM = ""
    var tripEndImage: File? = null
    var tripEndImagePath: String? = null
    fun endTrip() {
        if (isOutStationTrip.get() || isRentalTrip.get())
            getNavigator().openMeterUploadDialog(1)
        else
            callEndTripApi()
    }


    /*
    Get current Location
     */
    fun getLastLocation(context: Context) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient!!.lastLocation.addOnSuccessListener { location: Location? ->
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                session.saveString(SessionMaintainence.CURRENT_LATITUDE, "" + location.latitude)
                session.saveString(SessionMaintainence.CURRENT_LONGITUDE, "" + location.longitude)
                currentLatlng.set(LatLng(location.latitude, location.longitude))
                googleMap?.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            location.latitude,
                            location.longitude
                        ), 16f
                    )
                )
            }
        }
    }

    /**
     * load current location when page opens.
     */
    private var lastAnimatedTime: Long = 0
    fun animateMarker(latLng: LatLng?, location: Location?) {
        if (System.currentTimeMillis() - lastAnimatedTime > 3000) {
            lastAnimatedTime = System.currentTimeMillis()
            if (driverMarker != null) {
//                previousLocation?.bearingTo(location)?.let {
//                    driverMarker?.rotation =it
//                }
//                driverMarker?.rotation = location?.bearing!!
                latLng?.let { ll ->
//                    if(previousLatLng != null)
//                    buildBound(currentLatlng.get(),previousLatLng , 0.40)
//                    else
//                        buildBound(currentLatlng.get(),pickupLatlng.get(),0.40)
//                   googleMap ?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f),)
                    val cameraPosition = CameraPosition.Builder()
                        .target(ll)
                        .zoom(16f)
                        .build()
                    googleMap?.animateCamera(
                        CameraUpdateFactory.newCameraPosition(cameraPosition),
                        3000,
                        null
                    )
//                    val center = CameraUpdateFactory.newLatLng(it)
//                    googleMap?.animateCamera(center)
                    location?.bearing?.let {
                        driverMarker?.rotation = it
                    }
                    val latLngInterpolator: LatLngInterpolator = LatLngInterpolator.Linear()
                    MarkerAnimation.animateMarker(driverMarker, latLng, latLngInterpolator)
                }
            } else {
                googleMap?.let { map ->
                    latLng?.let {
                        val bitmapDescriptorFactory =
                            BitmapDescriptorFactory.fromResource(R.drawable.car_icon_new)

                        driverMarker = map.addMarker(
                            MarkerOptions()
                                .position(it)
                                .icon(bitmapDescriptorFactory)
                        )!!

                    }

                }
            }

        }
    }


    var showCallButton = ObservableBoolean(true)
    var showGoogleMapNavButton = ObservableBoolean(true)
    var tripType = ""
    var isOutstationTwoWay = false

    /**
     * load data from requestInProgress
     */
    fun loadRequestData(
        string: String?,
        driverData: String?
    ) {
        if (session.getString(SessionMaintainence.SAVED_DISTANCE_TRAVELLED)?.isNotEmpty() == true) {
            tripDistance.set(
                "${
                    round(
                        session.getString(SessionMaintainence.SAVED_DISTANCE_TRAVELLED)!!
                            .toDouble(), 3
                    )
                }"
            )
        }
        val driverDatass = HashMap<String, Any>()
        requestData = Gson().fromJson(string, RequestResponseData::class.java)
        driverRequestData = Gson().fromJson(driverData, BaseResponse.ReqDriverData::class.java)

        driverRequestData.let {
            driverDatass["is_available"] = false
            driverRef?.child(driverRequestData.slug!!)
                ?.updateChildren(driverDatass)
        }
        requestData.is_instant_trip?.let { isInstantJob.set(it == 1) }
        isInstantJob.set(requestData.is_instant_trip == 1)
        requestData.driver?.profilePic?.let {
             profileImage.set(it)
        //    getImageFromS3(it, 0)

        }
        requestData.outstationTripType?.let { isOutstationTwoWay = it.equals("TWO", true) }
        requestData.userUploadImage?.let { isUserPicUpload.set(it) }
        requestData.driverUploadImage?.let { isDriverPicUpload = it }
        requestData.startNightTime?.let { initialTime = it }
        requestData.endNightTime?.let { endTime = it }
        requestData.skipNightPhoto?.let { isNightPhotoSkip = it }
        requestData.startKM?.let { stratKM.set(it) }
        requestData.pickLat?.let { lat ->
            requestData.pickLng?.let { lon -> pickupLatlng.set(LatLng(lat, lon)) }
        }
        requestData.serviceCategory?.let { tripType = it }
        requestData.dropLat?.let { lat ->
            requestData.dropLng?.let { lon -> dropLatlng.set(LatLng(lat, lon)) }
        }

        if (requestData.isTripStart == 1) {
            if (requestData.dropAddress != null)
                requestData.dropAddress.let { tripAddress.set(it) }
            else
                showAddress.set(false)
        } else {
            requestData.pickAddress.let { tripAddress.set(it) }
        }

        requestData.ifDispatch?.let { isDispatch.set(it == 1) }
        if (requestData.others != null) {
            requestData.others?.phoneNumber?.let { phoneNumber.set(it) }
        } else {
            requestData.user?.phoneNumber?.let { phoneNumber.set(it) }
        }
        reqID.set(requestData.id)
        requestData.user?.firstname?.let { fname ->
            requestData.user?.lastname?.let { lname -> userName.set(fname + lname) }
        }
        requestData.requestNumber.let { reqNumber.set(it) }

        driverRequestData.driver_notes.let {
            if (it.isNullOrEmpty()) {
                showNotes.set(false)
            } else {
                showNotes.set(true)
                userNotes.set(" $it")
            }
        }

        isCompleted.set(requestData.isCompleted == 1)
        isTripStarted.set(requestData.isTripStart == 1)
        if (isTripStarted.get())
            session.saveString(SessionMaintainence.IS_TRIP_STARTED, "TRUE")
        else
            session.saveString(SessionMaintainence.IS_TRIP_STARTED, "FALSE")
        requestData.graceWaitingTimeAfterStart?.let {
            graceStartLimit = it.toInt() * 60
        }
        requestData.graceWaitingTime?.let {
            graceArriveLimit = it.toInt() * 60
        }
        requestData.tripStartTime?.let { tripStartTime = it }
        requestData.serviceCategory?.let {
            tripType = it
            if (it.equals("OUTSTATION", true)) {
                isOutStationTrip.set(true)
                isOutsationOrRental.set(true)
            } else if (it.equals("RENTAL")) {
                isRentalTrip.set(true)
                isOutsationOrRental.set(true)
            }
        }

        if (tripType.equals("RENTAL", true) && isTripStarted.get()) {
            if (isArrived.get())
                showAddress.set(false)
            showGoogleMapNavButton.set(false)
            removeWaitingTimeCallBacks()
            tripTimeHandler.removeCallbacks(tripTimeRunner)
            setTimer()
        }
        isArrived.set(requestData.isDriverArrived == 1)
        if (!isArrived.get()) {
            Utilz.updateDriverTripStatus(0, session.getString(SessionMaintainence.ReqID)!!)
        }
        isCancelled.set(requestData.isCancelled == 1)
        tripPaymentMode.set(requestData.paymentOpt)
        if (requestData.userOverallRating != null)
            userRatingString.set("" + round(requestData.userOverallRating!!, 1))

        if (requestData.location_approve == 1) {
            driverRequestData.changed_location_lat?.let { lat ->
                driverRequestData.changed_location_long?.let { lon ->
                    changdeDropLatlng.set(LatLng(lat, lon))
                    changedDropAddress.set(driverRequestData.changed_location_adddress)
                    changedLocId.set("" + driverRequestData.changed_location_id)
                    getNavigator().openApproveAlert()
                }
            }
        }
        requestData.user?.profilePic?.let {
            //getImageFromS3(it, 1)
            getNavigator().loadProfile(it)
        }

        when (requestData.instant_phone_number) {
            null -> showProfile.set(false)
            else -> showProfile.set(true)
        }
        showCallButton.set(requestData.is_instant_trip == 0)

        stopLatLng.set(requestData.stops?.longitude?.let {
            requestData.stops!!.latitude?.let { it1 ->
                LatLng(
                    it1.toDouble(), it.toDouble()
                )
            }
        })
        setMarkers(pickupLatlng.get(), dropLatlng.get(), stopLatLng.get())
        handleRoutes()
        if (isInstantJob.get()) {
            when {
                dropLatlng.get() == null -> showGoogleMapNavButton.set(false)
                dropLatlng.get()!!.latitude == 0.0 -> showGoogleMapNavButton.set(false)
                else -> showGoogleMapNavButton.set(true)
            }
        }

        if (isInstantJob.get()) {
            if (shouldITakePhoto() && !isDriverPicUpload) {
                getNavigator().uploadNightImageBoth()
            }
        }
    }

    private fun openMap(lat: Double, lng: Double, context: Context) {
        if (!isGoogleMapsInstalled(context))
            getNavigator().showMessage("Google map is not available")
        else {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("google.navigation:q=$lat,$lng")
            )
            intent.setClassName(
                "com.google.android.apps.maps",
                "com.google.android.maps.MapsActivity"
            )
            context.startActivity(intent)
        }

    }


    private fun buildBound(latLng1: LatLng?, latLng2: LatLng?, padding: Double) {
        getNavigator().getCtx()?.let {
            val width: Int = it.resources.displayMetrics.widthPixels
            val padding = (width * padding).toInt()
            if (latLng1 != null && latLng2 != null) {
                val boundsBuilder = LatLngBounds.builder()
                    .include(latLng1)
                    .include(latLng2)
                googleMap!!.animateCamera(
                    CameraUpdateFactory.newLatLngBounds(
                        boundsBuilder.build(), padding
                    )
                )
            } else {
                googleMap!!.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        pickupLatlng.get()!!, 16.0f
                    )
                )
            }
        }
    }

    fun removeLocUpdate() {
        fusedLocationClient!!.removeLocationUpdates(mLocationCallback)
    }


    val updateFRB = object : Runnable {
        override fun run() {
            updateDistanceAndTimeInUser()
            mainHandler.postDelayed(this, 3000)
        }
    }

    /*
    Waiting time runner and its global params
     */
    var waitingTimeInMinutes = ""
    var isWaitingTimerRunning = ObservableBoolean(false)
    var isWaitingTimerStarted = ObservableBoolean(false)
    var isGraceArriveRunning = false
    var isGraceStartRunning = false

    private val waitingTimeRunner = object : Runnable {
        override fun run() {
            isWaitingTimerStarted.set(true)
            val hours: Int = seconds / 3600
            val minutes: Int = seconds % 3600 / 60
            val secs: Int = seconds % 60

            // Format the seconds into hours, minutes,
            // and seconds.
            val time: String = java.lang.String
                .format(
                    Locale.getDefault(),
                    "%d:%02d:%02d", hours,
                    minutes, secs
                )

            printE("timer---", "__" + time)

            waitingTime.set(time)
            waitingTimeInMinutes = "${seconds / 60}"
            /*
            In case if app is killed the last updated total seconds is saved and to continue when trip page open
             */
            session.saveInt(SessionMaintainence.SAVED_WAITING_TIME, seconds)
            session.saveString(SessionMaintainence.DISPLAY_WAITING, time)
            // If running is true, increment the
            // seconds variable.
            if (isWaitingTimerRunning.get()) {
                seconds++
            }
            // Post the code again
            // with a delay of 1 second.
            mainHandler.postDelayed(this, 1000)
        }
    }


    private val graceRunnerArrive = object : Runnable {
        override fun run() {
            Log.e(
                "WaitingTime",
                "Grace runner before start running..${graceArriveTime} , limit${graceArriveLimit}"
            )
            graceArriveTime++
            session.saveInt(SessionMaintainence.SAVED_GRACE_TIME, graceArriveTime)
            if (graceArriveTime >= graceArriveLimit)
                session.saveBoolean(SessionMaintainence.IS_GRACE_TIME_COMPLETED, true)
            mainHandler.postDelayed(this, 1000)
        }
    }

    private val graceRunnerStart = object : Runnable {
        override fun run() {
            Log.e(
                "WaitingTime",
                "Grace runner after start running..${graceStartTime} , limit${graceStartLimit}"
            )
            graceStartTime++
            session.saveInt(SessionMaintainence.SAVED_GRACE_TIME_AFTER_START, graceStartTime)
            if (graceStartTime >= graceStartLimit)
                session.saveBoolean(SessionMaintainence.IS_GRACE_AFTER_START_COMPLETED, true)
            mainHandler.postDelayed(this, 1000)
        }
    }

    fun removeWaitingTimeCallBacks() {
        mainHandler.removeCallbacks(graceRunnerArrive)
        mainHandler.removeCallbacks(graceRunnerStart)
        mainHandler.removeCallbacks(waitingTimeRunner)
        isWaitingTimerStarted.set(false)
        isWaitingTimerRunning.set(false)
        isGraceArriveRunning = false
        isGraceStartRunning = false
    }

    /*
    Handle waiting time and its global params
     */
    var speed = 0

    fun handleWaitingTime(location: Location) {
//        if (isArrived.get()) {
//            speed = ((location.speed * 3600) / 1000).toInt()
//            printE(TripFragment.TAG, "$speed")
//            if (speed < 3)
//                speed = 0
//
//            if (speed < 3) {
//                if (session.getBoolean(SessionMaintainence.IS_GRACE_TIME_COMPLETED)) {
//                    mainHandler.removeCallbacks(graceTimeRunner)
//                    if (!isWaitingTimerRunning.get()) {
//                        isWaitingTimerRunning.set(true)
//                        mainHandler.post(waitingTimeRunner)
//                    }
//                } else {
//                    if (!isGraceTimerRunning) {
//                        isGraceTimerRunning = true
//                        mainHandler.post(graceTimeRunner)
//                    }
//                }
//
//            } else {
//                removeWaitingTimeCallBacks()
//            }
//        }

        speed = ((location.speed * 3600) / 1000).toInt()
        if (speed < 3) {
            if (isTripStarted.get()) {
                if (session.getBoolean(SessionMaintainence.IS_GRACE_AFTER_START_COMPLETED)) {
                    mainHandler.removeCallbacks(graceRunnerStart)
                    if (!isWaitingTimerRunning.get()) {
                        isWaitingTimerRunning.set(true)
                        mainHandler.post(waitingTimeRunner)
                    }
                } else {
                    if (isWaitingTimerRunning.get()) {
                        isWaitingTimerRunning.set(false)
                        mainHandler.removeCallbacks(waitingTimeRunner)
                    }
                    if (!isGraceStartRunning) {
                        isGraceStartRunning = true
                        mainHandler.post(graceRunnerStart)
                    }
                }
            } else if (isArrived.get()) {
                if (session.getBoolean(SessionMaintainence.IS_GRACE_TIME_COMPLETED)) {
                    mainHandler.removeCallbacks(graceRunnerArrive)
                    if (!isWaitingTimerRunning.get()) {
                        isWaitingTimerRunning.set(true)
                        mainHandler.post(waitingTimeRunner)
                    }
                } else {
                    if (!isGraceArriveRunning) {
                        isGraceArriveRunning = true
                        mainHandler.post(graceRunnerArrive)
                    }
                }
            }
        } else {
            removeWaitingTimeCallBacks()
        }
    }

    fun updateDistanceAndTimeInUser() {
        if (reqID.get() != null) {
            val requestData = HashMap<String, Any>()
            if (tripType.equals("RENTAL", true)) {
                requestData["rental_trip_time"] = tripTime.get()!!
            } else {
                requestData["waiting_time"] = waitingTime.get()!!
            }
            requestData["distancee"] = tripDistance.get()!!
            requestRef?.child(session.getString(SessionMaintainence.ReqID)!!)
                ?.updateChildren(requestData)

        }

    }


    fun getAddressAndEndTrip() {
        mapsHelper.GetAddressFromLatLng(
            currentLatlng.get()?.latitude.toString() + "," + currentLatlng.get()?.longitude,
            false, session.getString(SessionMaintainence.GEOCODE_DYNAMIC_KEY)
        )!!.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.isSuccessful) {
                    if (response.body() != null && response.body()!!
                            .getAsJsonArray("results") != null
                    ) {
                        val status = response.body()!!["status"].asString
                        if (status == "OK") {
                            currentAddress.set(
                                response.body()!!
                                    .getAsJsonArray("results")[0].asJsonObject["formatted_address"].asString
                            )
                            endTrip()
                        } else {
                            getAddGeoCode()
                        }
                    } else
                        getAddGeoCode()
                } else
                    getAddGeoCode()
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                getAddGeoCode()
            }
        })

    }

    fun getAddGeoCode() {
        if (Geocoder.isPresent()) {
            try {
                val geocoder = getNavigator().getCtx()?.let { Geocoder(it) }
                var mAddress = ""
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    geocoder?.getFromLocation(
                        currentLatlng.get()!!.latitude,
                        currentLatlng.get()!!.longitude,
                        1
                    ) { addresses ->
                        if (addresses.size > 0) {
                            if (addresses[0].getAddressLine(0) != null) {
                                mAddress = addresses[0].getAddressLine(0)
                            }
                            if (addresses[0].getAddressLine(1) != null) {
                                mAddress = mAddress + ", " + addresses[0].getAddressLine(1)
                            }
                            if (addresses[0].getAddressLine(2) != null) {
                                mAddress = mAddress + ", " + addresses[0].getAddressLine(2)
                            }
                        }
                        currentAddress.set(mAddress)
                        if (currentAddress.get()!!.isNotEmpty())
                            endTrip()
                        else
                            getNavigator().showMessage(translationModel?.txt_please_try_again ?: "")
                    }
                } else {
                    val addresses = geocoder?.getFromLocation(
                        currentLatlng.get()!!.latitude,
                        currentLatlng.get()!!.longitude,
                        1
                    )
                    if (addresses != null && addresses.size > 0) {
                        if (addresses[0].getAddressLine(0) != null) {
                            mAddress = addresses[0].getAddressLine(0)
                        }
                        if (addresses[0].getAddressLine(1) != null) {
                            mAddress = mAddress + ", " + addresses[0].getAddressLine(1)
                        }
                        if (addresses[0].getAddressLine(2) != null) {
                            mAddress = mAddress + ", " + addresses[0].getAddressLine(2)
                        }
                    }

                    currentAddress.set(
                        mAddress
                    )
                    if (currentAddress.get()!!.isNotEmpty())
                        endTrip()
                    else
                        getNavigator().showMessage(translationModel?.txt_please_try_again ?: "")
                }
            }catch (e: java.lang.Exception) {
                e.printStackTrace()
                getNavigator().showMessage(translationModel?.txt_please_try_again ?: "")
            }
        } else {
            getNavigator().showMessage(translationModel?.txt_please_try_again ?: "")
        }


    }


    private fun clearTimeAndDistanceAfterTripCompleted() {
        session.saveBoolean(SessionMaintainence.IS_GRACE_TIME_COMPLETED, false)
        session.saveBoolean(SessionMaintainence.IS_GRACE_AFTER_START_COMPLETED, false)
        session.saveInt(SessionMaintainence.SAVED_WAITING_TIME, 0)
        session.saveInt(SessionMaintainence.SAVED_GRACE_TIME, 0)
        session.saveInt(SessionMaintainence.SAVED_GRACE_TIME_AFTER_START, 0)
        session.saveString(SessionMaintainence.SAVED_DISTANCE_TRAVELLED, "")
        session.saveString(SessionMaintainence.DISPLAY_WAITING, "0.0")
    }

    var showTripTime = ObservableBoolean(false)
    private fun setTimer() {
        if (tripStartTime.isNotEmpty())
            calculateTimeDifference()
        tripTimeHandler.post(tripTimeRunner)
        showTripTime.set(true)
        updateDistanceAndTimeInUser()
    }

    private fun calculateTimeDifference() {
        try {
            val mDate = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH).parse(tripStartTime)
            val startDate: Date? = mDate
            val endDate = Date()

            val diffInMs = endDate.time - startDate!!.time
            tripTimeInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMs).toInt()
        } catch (e: ParseException) {

        }

    }

    var tripTimeInSec = 0
    var tripTime = ObservableField("")
    val tripTimeHandler = Handler(Looper.getMainLooper())
    private val tripTimeRunner = object : Runnable {
        override fun run() {
            if (tripTimeInSec > 0) {
                val hours: Int = tripTimeInSec / 3600
                val minutes: Int = tripTimeInSec % 3600 / 60
                val secs: Int = tripTimeInSec % 60

                // Format the seconds into hours, minutes,
                // and seconds.
                val time: String = java.lang.String
                    .format(
                        Locale.getDefault(),
                        "%d:%02d:%02d", hours,
                        minutes, secs
                    )

                printE("trip_timer---", "__" + time)
                tripTime.set(time)
            }
            tripTimeInSec++

            // Post the code again
            // with a delay of 1 second.
            tripTimeHandler.postDelayed(this, 1000)
        }

    }

    private fun handleRoutes() {
        if (!isArrived.get()) {
            /* draw path from driver current location to pickup location */
            if (session.getString(SessionMaintainence.DRIVER_TO_PICKUP_POLY)!!.isEmpty()) {
                /* get poly string for driver location to pick up */
                if (session.getString(SessionMaintainence.CURRENT_LATITUDE)!!
                        .isNotEmpty() && session.getString(SessionMaintainence.CURRENT_LONGITUDE)!!
                        .isNotEmpty()
                ) {
                    pickupLatlng.get()?.let { pickup ->
                        getPolyStringAndDrawPath(
                            LatLng(
                                session.getString(SessionMaintainence.CURRENT_LATITUDE)!!
                                    .toDouble(),
                                session.getString(SessionMaintainence.CURRENT_LONGITUDE)!!
                                    .toDouble()
                            ), pickup
                        )
                    }

                }
                currentLatlng.get()?.let { current ->
                }
            } else {
                drawDirection(session.getString(SessionMaintainence.DRIVER_TO_PICKUP_POLY))
            }
        } else {
            /* draw pickup to drop location using poly_string */
            session.saveString(SessionMaintainence.DRIVER_TO_PICKUP_POLY, "")
            if (requestData.polyString != null)
                drawDirection(requestData.polyString)
            else
                getPoly()

        }
    }


    private fun drawDirection(encodedPoints: String?) {
        if (encodedPoints != null) {
            pointsDest = ArrayList()
            val polyz: List<LatLng> = Utilz.decodeOverviewPolyLinePoints(encodedPoints)
            pointsDest?.addAll(polyz)

            lineOptionsDest1 = PolylineOptions()
            lineOptionDesDark = PolylineOptions()
            lineOptionsDest1!!.geodesic(true)
            lineOptionDesDark!!.geodesic(true)

            polyLineDest1?.remove()
            pointsDest?.let { lineOptionsDest1?.addAll(it) }
            lineOptionsDest1?.width(10f)
            lineOptionsDest1?.startCap(SquareCap())
            lineOptionsDest1?.endCap(SquareCap())
            lineOptionsDest1?.jointType(JointType.ROUND)
            getNavigator().getCtx()?.let {
                lineOptionsDest1?.color(
                    ContextCompat.getColor(it, R.color.colorPrimary)
                )
                lineOptionDesDark?.color(
                    ContextCompat.getColor(it, R.color.colorPrimary)
                )
            }

            try {
                lineOptionsDest1?.let {
                    lineOptionDesDark?.let { dark ->
                        polyLineDest1 = googleMap?.addPolyline(it)
                        polyLineDestDark = googleMap?.addPolyline(dark)
                        buildBound(pickupLatlng.get(), dropLatlng.get(), 0.10)
//                        animatePolyLine(polyz)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    private fun setMarkers(pickLatLng: LatLng?, dropLatLng: LatLng?, stopLatLng: LatLng?) {
        if (pickupMarker == null) {
            pickLatLng?.let {
                pickupMarker =
                    googleMap?.addMarker(
                        MarkerOptions()
                            .position(it)
                            .title("Pickup Location")
                            .anchor(0.5f, 0.5f)
                            .icon(getNavigator().getMarkerIcon(R.drawable.ic_pickup_streched))
                    )
            }
        }

        if (dropMarker == null) {
            dropLatLng?.let {
                dropMarker =
                    googleMap!!.addMarker(
                        MarkerOptions()
                            .position(it)
                            .title("Drop Location")
                            .anchor(0.5f, 0.5f)
                            .icon(getNavigator().getMarkerIcon(R.drawable.ic_drop_streched))
                    )
            }

        }

        if (stopMarker == null) {
            stopLatLng?.let {
                googleMap!!.addMarker(
                    MarkerOptions()
                        .position(it)
                        .title("Stop Location")
                        .anchor(0.5f, 0.5f)
                        .icon(getNavigator().getMarkerIcon(R.drawable.ic_drop_streched))
                )
            }
        }
    }

    private fun getPolyStringAndDrawPath(origin: LatLng, destination: LatLng) {
        mapsHelper.GetDrawpath(
            origin.latitude.toString() + "," + origin.longitude,
            destination.latitude.toString() + "," + destination.longitude, false,
            session.getString(SessionMaintainence.DIRECTION_DYNAMIC_KEY)
        )!!.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.body() != null) {
                    routeDest = Route()
                    Utilz.parseRoute(response.body()!!, routeDest!!)
                    routeDest?.polyPoints?.let {
                        session.saveString(SessionMaintainence.DRIVER_TO_PICKUP_POLY, it)
                        drawDirection(session.getString(SessionMaintainence.DRIVER_TO_PICKUP_POLY))
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                getNavigator().showMessage(t.message!!)
            }
        })
    }

    /* The below method will be called if poly string  is null*/
    private fun getPoly() {
        if (requestData.stops != null) {
            if (pickupLatlng.get() != null && stopLatLng.get() != null && dropLatlng.get() != null) {
                mapsHelper.getDrawPathMultipleStops(
                    pickupLatlng.get()?.latitude.toString() + "," + pickupLatlng.get()?.longitude,
                    dropLatlng.get()?.latitude.toString() + "," + dropLatlng.get()?.longitude,
                    stopLatLng.get()?.latitude.toString() + "," + stopLatLng.get()?.longitude,
                    false,
                    session.getString(SessionMaintainence.DIRECTION_DYNAMIC_KEY)
                )!!.enqueue(object : Callback<JsonObject?> {
                    override fun onResponse(
                        call: Call<JsonObject?>,
                        response: Response<JsonObject?>
                    ) {
                        if (response.body() != null) {
                            routeDest = Route()
                            Utilz.parseRoute(response.body()!!, routeDest!!)
                            routeDest?.polyPoints?.let {
                                drawDirection(it)
                            }
                        }
                    }

                    override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                        getNavigator().showMessage(t.message!!)
                    }
                })
            }
        } else {
            if (pickupLatlng.get() != null && dropLatlng.get() != null) {
                mapsHelper.GetDrawpath(
                    pickupLatlng.get()!!.latitude.toString() + "," + pickupLatlng.get()!!.longitude,
                    dropLatlng.get()!!.latitude.toString() + "," + dropLatlng.get()!!.longitude,
                    false, session.getString(SessionMaintainence.DIRECTION_DYNAMIC_KEY)
                )!!.enqueue(object : Callback<JsonObject?> {
                    override fun onResponse(
                        call: Call<JsonObject?>,
                        response: Response<JsonObject?>
                    ) {
                        if (response.body() != null) {
                            routeDest = Route()
                            Utilz.parseRoute(response.body()!!, routeDest!!)
                            routeDest?.polyPoints?.let {
                                drawDirection(it)
                            }
                        }
                    }

                    override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                        getNavigator().showMessage(t.message!!)
                    }
                })
            }

        }
    }

    private fun clearReqId() {
        val reqId = session.getString(SessionMaintainence.ReqID)
        if (reqId != null) {
            requestRef?.child(reqId)
                ?.removeValue()
                ?.addOnSuccessListener { session.saveString(SessionMaintainence.ReqID, "") }
        }
    }

    private var initialTime = ""
    private var endTime = ""
    private fun shouldITakePhoto(): Boolean {
        val currentTime = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(Date())
        return if (Utilz.isTimeBetweenTwoTime(
                initialTime,
                "23:59:59",
                currentTime
            )
        ) true else Utilz.isTimeBetweenTwoTime("00:00:00", endTime, currentTime)
    }


    /* The below method  getPolyStringAndAcceptDropChange will get poly string for driver current location  to changed drop location */
    fun getPolyStringAndAcceptDropChange(
        origin: LatLng,
        destination: LatLng,
        stringExtra: String,
        loc_id: String,
        status: Int
    ) {
        mapsHelper.GetDrawpath(
            origin.latitude.toString() + "," + origin.longitude,
            destination.latitude.toString() + "," + destination.longitude, false,
            session.getString(SessionMaintainence.DIRECTION_DYNAMIC_KEY)
        )!!.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                if (response.body() != null) {
                    routeDest = Route()
                    routeDest = Utilz.parseRoute(response.body()!!, routeDest!!)
                    approveLocation(stringExtra, loc_id, status, routeDest?.polyPoints ?: "")
                }
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                getNavigator().showMessage(t.message!!)
                approveLocation(stringExtra, loc_id, status, routeDest?.polyPoints ?: "")
            }
        })

    }

    var cameraFlag = 0

    /*
    1-> for driver image
    2-> for driverImage for dispatcher or instant trip
    3-> for userImage for dispatcher or instant trip
     */
    private fun getEndTime(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(Date())
    }

}

@BindingAdapter("driverProfilePicInTrip")
fun setImage(imageView: ImageView, url: String?) {
    Glide.with(imageView.context).load(url)
        .apply(
            RequestOptions.circleCropTransform().error(R.drawable.ic_user)
                .placeholder(R.drawable.ic_user)
        )
        .into(imageView)
}

@BindingAdapter("setImages")
fun setImageUrl(imageView: ImageView, url: String?) {
    val vJob = Job()
    val uiScope = CoroutineScope(Dispatchers.Main + vJob)
    uiScope.launch {
        withContext(Dispatchers.Main) {
            imageView.setPadding(0, 0, 0, 0)
            val context = imageView.context
            Glide.with(context).load(url).apply(
                RequestOptions().error(R.drawable.ic_user)
                    .placeholder(R.drawable.ic_user)
            ).into(imageView)
        }
    }
}

@BindingAdapter("drawableLeft")
fun drawableLeft(view: TextView, drawable: Drawable) {
    setIntrinsicBounds(drawable)
    val drawables = view.compoundDrawables
    view.setCompoundDrawables(drawable, drawables[1], drawables[2], drawables[3])
}

private fun setIntrinsicBounds(drawable: Drawable?) {
    drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
}
