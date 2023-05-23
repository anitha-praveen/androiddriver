package com.rodaClone.driver.drawer

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.core.GeoHash
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.rodaClone.driver.BuildConfig
import com.rodaClone.driver.base.BaseVM
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.connection.ConnectionHelper
import com.rodaClone.driver.connection.CustomException
import com.rodaClone.driver.connection.DocumentExpiry
import com.rodaClone.driver.connection.responseModels.Route
import com.rodaClone.driver.ut.*
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URL
import java.util.*
import javax.inject.Inject

class DrawerVM @Inject constructor(
    val session: SessionMaintainence,
    val mConnect: ConnectionHelper,
    private val mapsHelper: MapsHelper
) :
    BaseVM<BaseResponse, DrawerNavigator>(session, mConnect),
    SocketHelper.Companion.SocketListener {

    var map: HashMap<String, String> = HashMap()
    var TAG: String = "DrawerVM"

    var phone = ObservableField("")
    var countryCode = ObservableField("")
    var firstName = ObservableField("")
    var lastName = ObservableField("")
    var completed = ObservableField("")
    var cancelled = ObservableField("")
    var earnings = ObservableField("")
    var wallet = ObservableField("")
    var walletColor = ObservableBoolean(false)
    var showProfilePic = ObservableField(true)
    var driverData: BaseResponse.ReqDriverData? = null
    var reqTripsData: BaseResponse.MetaData? = null

    var newLocation: Location? = null
    var oldLocation: Location? = null
    var setToglleStatus = ObservableBoolean(true)
    var typeSlug = ObservableField("")
    var xxx = ObservableField(0)
    lateinit var driverStatus: BaseResponse.OnlineOfflineData
    var showSubscribeInApproval = ObservableBoolean(false)
    var documentExpiryList = arrayListOf<DocumentExpiry>()
    var docuExpiredString = ObservableField("")
    var serviceCategory = ObservableField("")
    var ref: DatabaseReference? = null
    var requestRef: DatabaseReference? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var isBlocked: Boolean? = null
    var reqInproData:BaseResponse.DataObjectsAllApi? = null


    /*
   apiCall value 0 -> no Api called
                 1 ->  request in progress api
                 2 ->  Toggle driver status API
                 3 ->  calling logout API
    */
    var apiCall = 0
    var referralCode = ObservableField("")

    init {
        var database = FirebaseDatabase.getInstance()

        ref = database.getReference("drivers")
        requestRef = database.getReference("requests")
    }


    /* click events */
    fun onClickCurrentLoc() {
        getNavigator().currentLoc()
    }

    fun onMenuClick() {
        getNavigator().openSideMenu()
    }

    /* api calls */
    fun requestInProgressApi() {
        if (getNavigator().isNetworkConnected()) {
            apiCall = 1
            driverReqProgressBase()

        } else getNavigator().showNetworkUnAvailable()

    }

    fun toggleStatus() {
        if (getNavigator().isNetworkConnected()) {
            apiCall = 2
            driverStatus()
        } else getNavigator().showNetworkUnAvailable()
    }

    fun getLogutApiVm() {
        if (getNavigator().isNetworkConnected()) {
            apiCall = 3
            getLogOutApiBase()
        } else getNavigator().showNetworkUnAvailable()

    }

    override fun onSuccessfulApi(response: BaseResponse?) {
        isLoading.value = false

        if (apiCall == 1 /* request in progress api */) {
            if (response?.data != null) {
                val reqInProgData: BaseResponse.DataObjectsAllApi =
                    Gson().fromJson(
                        Gson().toJson(response.data),
                        BaseResponse.DataObjectsAllApi::class.java
                    )
                driverData = reqInProgData.driver!!
                reqInproData = reqInProgData
                session.saveDriverData(
                    SessionMaintainence.DriverData,
                    Gson().toJson(driverData)
                )

                session.saveReqTripData(
                    SessionMaintainence.ReqTripData,
                    ""
                )
                firstName.set(driverData!!.firstname)
                lastName.set(driverData!!.lastname)

                serviceCategory.set(
                    driverData!!.serviceCategory!!
                )
                printE("serviceCate--", "__" + serviceCategory.get())
                if (driverData!!.profilePic != null) {
                 //   getImageFromS3(driverData!!.profilePic!!)
                    getNavigator().setImage(driverData!!.profilePic!!)
                }
                referralCode.set(driverData!!.referralCode)
                typeSlug.set(driverData!!.typeId)

                completed.set(translationModel!!.txt_completed + ": " + driverData!!.completed)
                cancelled.set(translationModel!!.txt_cancelled + ": " + driverData!!.cancelled)

                driverData?.walletAmount?.let {
                    if (it.toDouble() < -450)
                        walletColor.set(true)
                }

                wallet.set(driverData!!.currencySymbol + driverData!!.walletAmount)
                earnings.set(
                    driverData!!.currencySymbol + driverData!!.tdyEarnings?.toDouble()?.let {
                        round(
                            it, 3
                        )
                    })

                setToglleStatus.set(driverData!!.driverStatus == 1)
                session.saveString(SessionMaintainence.DriverID, driverData!!.slug!!)
                session.saveString(SessionMaintainence.TYPE_ID, typeSlug.get()!!)
                session.saveBoolean(
                    SessionMaintainence.DriverStatus,
                    (driverData!!.driverStatus == 1)
                )
                phone.set(session.getString(SessionMaintainence.SelectedCountryCode) + driverData!!.phoneNumber)

                setToglleStatus.set(driverData!!.driverStatus == 1 && driverData!!.approveStatus == 1)

                showSubscribeInApproval.set(driverData!!.subscriptionType == 2 && driverData!!.subscriptionStatus == false)


                val intent = Intent(Config.RECEIVE_SUBSCRIPTION_DETAILS)
                val subscriptionStatus: Boolean =
                    if (driverData?.subscriptionStatus == null) false else driverData?.subscriptionStatus != null && driverData?.subscriptionStatus == true
                val showSub: Boolean =
                    driverData!!.subscriptionType == 2 || driverData!!.subscriptionType == 3 && subscriptionStatus
                intent.putExtra(Config.SHOW_SUBSCRIBE, showSub)
                if (driverData!!.subscriptionType == 2 || driverData!!.subscriptionType == 3 && subscriptionStatus)
                    intent.putExtra(
                        Config.BALANCE_SUBSCRIPTION_DAYS,
                        driverData!!.subscription?.balanceDays
                    )
                LocalBroadcastManager.getInstance(getNavigator().getCtx()).sendBroadcast(intent)

                if (driverData!!.approveStatus == 0) {
                    /*
                   subscriptionType 1 -> COMMISSION
                                    2 -> SUBSCRIPTION
                                    3 -> BOTH
                    */
                    documentExpiryList.addAll(driverData!!.documentExpiry)

                    getNavigator().openApproveDeclinePage(
                        driverData!!.approveStatus,
                        driverData!!.documentStatus,
                        driverData!!.blockReason,
                        showSubscribeInApproval.get(),
                    )
                    isBlocked = true
                    getNavigator().setFloatService(false)
                } else {
                    isBlocked = false
                    getNavigator().setFloatService(driverData!!.driverStatus == 1)
                    if (driverData!!.metaData != null) {
                        /* Clear previous trip values if present and do not clear reqId here */
//                        session.clearTripDetails()
                        session.saveBoolean(SessionMaintainence.IS_GRACE_TIME_COMPLETED, false)
                        session.saveBoolean(
                            SessionMaintainence.IS_GRACE_AFTER_START_COMPLETED,
                            false
                        )
                        session.saveString(SessionMaintainence.IS_TRIP_STARTED, "FALSE")
                        session.saveString(SessionMaintainence.SAVED_TRIP_LAT, "")
                        session.saveString(SessionMaintainence.SAVED_TRIP_LNG, "")
                        session.saveString(SessionMaintainence.SAVED_DISTANCE_TRAVELLED, "")
                        Log.e("CheckFirst", "ReqProgress")
                        getNavigator().openAcceptRejectAct(driverData!!.metaData!!.data)
                        if (getNavigator().getOptimizingDialog()?.isShowing == true)
                            getNavigator().getOptimizingDialog()?.dismiss()
                        if (getNavigator().getMiPermission()?.isShowing == true)
                            getNavigator().getMiPermission()?.dismiss()
                    } else if (reqInProgData.tripsData != null) {
                        reqTripsData = reqInProgData.tripsData
                        session.saveString(SessionMaintainence.ReqID, reqTripsData!!.data!!.id!!)
                        session.saveReqTripData(
                            SessionMaintainence.ReqTripData,
                            Gson().toJson(reqTripsData)
                        )
                        if (reqTripsData?.data?.isCompleted == 1)
                            getNavigator().openInvoice(reqTripsData?.data!!)
                        else
                            getNavigator().openTripData(reqTripsData!!.data, reqInProgData.driver)
                        if (getNavigator().getOptimizingDialog()?.isShowing == true)
                            getNavigator().getOptimizingDialog()?.dismiss()
                        if (getNavigator().getMiPermission()?.isShowing == true)
                            getNavigator().getMiPermission()?.dismiss()
                        if (getNavigator().getmAlert()?.isShowing == true)
                            getNavigator().getmAlert()?.dismiss()
                    } else {
                        /* Clear previous trip values if present and clear request id */
                        session.saveString(SessionMaintainence.ReqID, "")
                        session.saveBoolean(SessionMaintainence.IS_GRACE_TIME_COMPLETED, false)
                        session.saveBoolean(
                            SessionMaintainence.IS_GRACE_AFTER_START_COMPLETED,
                            false
                        )
                        session.saveString(SessionMaintainence.IS_TRIP_STARTED, "FALSE")
                        session.saveString(SessionMaintainence.SAVED_TRIP_LAT, "")
                        session.saveString(SessionMaintainence.SAVED_TRIP_LNG, "")
                        session.saveString(SessionMaintainence.SAVED_DISTANCE_TRAVELLED, "")
                        if (driverData?.metaData == null)
                            getNavigator().checkUpdate()
                        getNavigator().openMapFragment()
                    }
                    updateDataToFirebase()
                }
                socketInitiation()
            }
        } else if (apiCall == 2 /* Toggle driver status API */) {
            /**
             * on/off response
             */
            driverStatus = Gson().fromJson(
                Gson().toJson(response!!.data),
                BaseResponse.OnlineOfflineData::class.java
            )
            session.saveBoolean(
                SessionMaintainence.DriverStatus,
                (driverStatus.onlineStatus == 1)
            )
            setToglleStatus.set(driverStatus.onlineStatus == 1)
            getNavigator().setFloatService(driverStatus.onlineStatus == 1)
            updateDataToFirebase()
        } else if (apiCall == 3 /* calling logout API */) {
            setToglleStatus.set(false)
            getNavigator().setFloatService(false)
            session.saveBoolean(SessionMaintainence.DriverStatus, false)
            updateDataToFirebase()
            SocketHelper.disConnectSocket()
            logout()
        }

    }

    /**
     * get Image from s3bucket
     */
    private fun getImageFromS3(profilePic: String) {
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
                    // update here in UI
                    getNavigator().setImage(url.toString())
                }
            }
        }

    }

    public fun updateDriverProfile(fName: String, lName: String, driverProfile: String) {
        firstName.set(fName)
        lastName.set(lName)
        getNavigator().setImage(driverProfile)


    }

    private fun socketInitiation() {

        if (SocketHelper.mSocket == null)
            SocketHelper.init(session, this)
        else {
            SocketHelper.disConnectSocket()
            SocketHelper.init(session, this)
        }


        //if (SocketHelper.mSocket == null)
//        SocketHelper.init(session, this)
//        else if (SocketHelper.mSocket!!.connected()) {
//            SocketHelper.mSocket!!.disconnect()
//            SocketHelper.mSocket!!.connect()
//        }
    }

    private fun updateDataToFirebase() {

        driverData?.let {
            val driverDataMap: MutableMap<String, Any?> = HashMap()
            driverDataMap["id"] = it.slug
            driverDataMap["first_name"] = firstName.get()
            driverDataMap["last_name"] = lastName.get()
            driverDataMap["is_active"] = setToglleStatus.get()
            driverDataMap["service_category"] = serviceCategory.get()!!.replace(" ", "")
            driverDataMap["phone_number"] = it.phoneNumber
            driverDataMap["is_available"] =
                session.getString(SessionMaintainence.ReqID).isNullOrEmpty()
            driverDataMap["type"] = it.typeId
            driverDataMap["app_version"] = BuildConfig.VERSION_NAME

            /*val auth = FirebaseAuth.getInstance().currentUser
            if (auth != null)*/
            ref!!.child(it.slug!!).updateChildren(driverDataMap)
        }
    }

    override fun onFailureApi(call: Call<BaseResponse?>?, t: CustomException?) {
        isLoading.value = false

        if (t!!.exception.equals("Token Expired", ignoreCase = true)) {
            translationModel?.txt_token_expired_alert?.let { getNavigator().showMessage(it) }
            session.saveString(SessionMaintainence.AccessToken, "")
            getNavigator().openLoginPage()
        } else getNavigator().showMessage(t.exception!!)

    }


    private fun logout() {
        getNavigator().callLogoutFromActivity()
    }

    fun locationUpdate(context: Context?) {
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(context!!)
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
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                printE(DrawerActivity.TAG, "Running location update")
                newLocation = location
                if (oldLocation == null)
                    oldLocation = newLocation

                session.saveString(SessionMaintainence.CURRENT_LATITUDE, "" + location.latitude)
                session.saveString(SessionMaintainence.CURRENT_LONGITUDE, "" + location.longitude)
                val geoHash = GeoHash(GeoLocation(location.latitude, location.longitude))
                val geoData = HashMap<String, Any>()


//                geoData["bearing"] = newLocation!!.bearingTo(oldLocation)
                geoData["bearing"] = newLocation!!.bearing
                val auth = FirebaseAuth.getInstance().currentUser

                if (session.getString(SessionMaintainence.ReqID).isNullOrEmpty()) {
                    printE("entryValuee---", "___entering")
                    geoData["g"] = geoHash.geoHashString
                    geoData["updated_at"] = ServerValue.TIMESTAMP
                    geoData["is_available"] = true
                    geoData["l"] = Arrays.asList(location.latitude, location.longitude)
                    if (driverData != null)
                        ref!!.child(driverData?.slug!!).updateChildren(geoData) { error, ref ->
                        }
                    clearTimeAndDistanceAfterTripCompleted()
                } else {
                    if (reqTripsData != null) {
                        geoData["lat"] = location.latitude
                        geoData["lng"] = location.longitude
                        geoData["id"] = driverData?.slug ?: ""
                        geoData["user_id"] = reqTripsData?.data?.user?.id ?: ""
                        geoData["request_id"] = session.getString(SessionMaintainence.ReqID) ?: ""

                        requestRef!!.child(session.getString(SessionMaintainence.ReqID) ?: "")
                            .updateChildren(geoData)
                    }

                }
                oldLocation = newLocation

            }
        }
    }

    override fun OnConnect() {

    }

    override fun OnDisconnect() {

    }

    override fun OnConnectError() {

    }

    override fun getRequest(data: String) {
        val response: BaseResponse = Gson().fromJson(data, BaseResponse::class.java)
        if (response.success_message?.equals("request_created", true) == true) {
            getNavigator().openAcceptRejectAct(response.result!!.data)
        } else if (response.result?.data?.isCancelled != null && response.result.data.isCancelled == 1) {
            Utilz.updateDriverTripStatus(
                4,
                response.result.data.id ?: session.getString(SessionMaintainence.ReqID)!!
            )
            session.saveString(SessionMaintainence.ReqID, "")
            val intent = Intent(Config.RECEIVE_CLEAR_DIST)
            LocalBroadcastManager.getInstance(getNavigator().getCtx()).sendBroadcast(intent)
            session.clearTripDetails()
            getNavigator().showTripCancelled()
        }
    }

    override fun isNetworkConnected(): Boolean {
        return getNavigator().isNetworkConnected()
    }

    override fun locationChanged(data: String) {
//        Log.e("dataSocket---", "socket--$data")
        val response: BaseResponse = Gson().fromJson(data, BaseResponse::class.java)
        response.result?.type?.let {
            val intent = Intent(Config.RECEIVE_TRIP_ADDRESS_CHANGE)
            if (it == 0) {
                intent.putExtra(Config.isPickup, false)
                intent.putExtra(Config.drop_address, response.result.address ?: "")
                intent.putExtra(Config.drop_lat, response.result.lattitude ?: "")
                intent.putExtra(Config.drop_lng, response.result.langitude ?: "")
                intent.putExtra(Config.loc_approve, response.result.locApprove ?: "")
                intent.putExtra(Config.loc_id, response.result.locationID ?: "")
                getNavigator().updateTripAddress(intent)
            } else {
                intent.putExtra(Config.isPickup, true)
                intent.putExtra(Config.pickupAddress, response.result.address ?: "")
                intent.putExtra(Config.pick_lat, response.result.lattitude ?: "")
                intent.putExtra(Config.pick_lng, response.result.langitude ?: "")
                response.result.lattitude?.let { lat ->
                    response.result.langitude?.let { lng ->
                        getRouteToPickUp(LatLng(lat.toDouble(), lng.toDouble()), intent)
                    }
                }
            }
        }
    }

    private fun getRouteToPickUp(pickLatLng: LatLng?, intent: Intent) {
        pickLatLng?.let { pickup ->
            getPolyString(
                LatLng(
                    session.getString(SessionMaintainence.CURRENT_LATITUDE)!!
                        .toDouble(),
                    session.getString(SessionMaintainence.CURRENT_LONGITUDE)!!
                        .toDouble()
                ), pickup, intent
            )
        }

    }

    var routeDest: Route? = null
    private fun getPolyString(origin: LatLng, destination: LatLng, intent: Intent) {
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
                    }
                }
                getNavigator().updateTripAddress(intent)
            }

            override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                getNavigator().showMessage(t.message!!)
                getNavigator().updateTripAddress(intent)
            }
        })
    }

    override fun packageChanged(data: String) {
        val response: BaseResponse = Gson().fromJson(data, BaseResponse::class.java)
        val intent = Intent(Config.RECEIVE_TRIP_DETAILS)
        intent.putExtra(Config.REQ_DATA, Gson().toJson(response.result?.data))
        intent.putExtra(Config.DRIVER_DATA, Gson().toJson(response.result?.data?.driver))
        LocalBroadcastManager.getInstance(getNavigator().getCtx()).sendBroadcast(intent)
    }

    override fun photoUploaded(data: String) {
        Log.e("onPhotoUpload","DrawerVM : "+ data)
        val response: BaseResponse = Gson().fromJson(data, BaseResponse::class.java)
        if (response.result?.retake_image == true) {
            val intent = Intent(Config.USER_NIGHT_PHOTO)
            intent.putExtra(Config.retake, true)
            LocalBroadcastManager.getInstance(getNavigator().getCtx()).sendBroadcast(intent)
        } else if (response.result?.uploadStatus == true) {
            val intent = Intent(Config.USER_NIGHT_PHOTO)
            intent.putExtra(Config.upload_status, response.result.uploadStatus)
            intent.putExtra(Config.images, response.result.uploadImageUrl)
            LocalBroadcastManager.getInstance(getNavigator().getCtx()).sendBroadcast(intent)
        }
    }


    fun clearTimeAndDistanceAfterTripCompleted() {
        session.saveBoolean(SessionMaintainence.IS_GRACE_TIME_COMPLETED, false)
        session.saveBoolean(SessionMaintainence.IS_GRACE_AFTER_START_COMPLETED, false)
        session.saveInt(SessionMaintainence.SAVED_WAITING_TIME, 0)
        session.saveInt(SessionMaintainence.SAVED_GRACE_TIME, 0)
        session.saveInt(SessionMaintainence.SAVED_GRACE_TIME_AFTER_START, 0)
        session.saveString(SessionMaintainence.SAVED_DISTANCE_TRAVELLED, "")
        session.saveString(SessionMaintainence.DISPLAY_WAITING, "0.0")
    }

}