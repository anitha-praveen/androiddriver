package com.rodaClone.driver.ut

import android.Manifest
import android.app.*
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PixelFormat
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.work.Constraints
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
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
import com.google.maps.android.SphericalUtil
import com.rodaClone.driver.MyApplication
import com.rodaClone.driver.R
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.drawer.DrawerActivity
import com.rodaClone.driver.drawer.DrawerActivity.Companion.ACTION_STOP_FOREGROUND
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


class FloatingService : Service() {
    private var windowManager: WindowManager? = null

    //    private var floatingControlView: ViewGroup? = null
    var iconHeight = 0
    var iconWidth = 0
    private var screenHeight = 0
    private var screenWidth = 0
    private val excecuter: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    var context: Context? = null
    var ref: DatabaseReference? = null
    var requestRef: DatabaseReference? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var isAdded = false
    private var fromPush = false

    private var currentTime: Long = 0
    private var removedTime: Long = 0
    private var timeDifference: Long = 0

    private val sessionMaintainence = MyApplication.appContext?.let { SessionMaintainence(it) }

    companion object {
        var isServiceStart = false
    }


    private lateinit var sessionM: SharedPreferences

    private val clearDist: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.e("FloatingService", "clearDistance")
            clearTimeAndDistanceAfterTripCompleted()
        }
    }

    override fun onCreate() {
        super.onCreate()
        isServiceStart = true
        context = this
        val keyGenParameterSpec =
            MasterKey.Builder(this).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
        sessionM = EncryptedSharedPreferences.create(
            this,
            Config.sharedPrefName,
            keyGenParameterSpec,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        generateForegroundNotification()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            getBubbleState, IntentFilter(Config.RECEIVE_BUBBLE_STATE)
        )

        LocalBroadcastManager.getInstance(this).registerReceiver(
            clearDist, IntentFilter(Config.RECEIVE_CLEAR_DIST)
        )

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("FloatingService", "onStartCommand")
        val bundle = intent?.extras
        if (bundle != null) {
            val data = bundle.getBoolean("fromPush")
            if (data)
                fromPush = true
        }
        if (windowManager == null) {
            windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        }
//        if (MyApplication.floatingControlView == null) {
//            val li = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
//            MyApplication.floatingControlView =
//                li.inflate(R.layout.layout_floating_control_view, null) as ViewGroup?
//        }

        if (intent?.action != null && intent.action.equals(
                ACTION_STOP_FOREGROUND, ignoreCase = true
            )
        ) {
            //  if (!excecuter.isShutdown)
            sessionMaintainence?.saveBoolean(SessionMaintainence.ISSERVICESTARTED, false)
            Log.e(
                "isServiceStarted",
                sessionMaintainence?.getBoolean(SessionMaintainence.ISSERVICESTARTED).toString()
            )
            excecuter.shutdownNow()
            removeFloatingContro()
            stopForeground(true)
            stopSelf()
        } else {
            sessionMaintainence?.saveBoolean(SessionMaintainence.ISSERVICESTARTED, true)
            Log.e(
                "isServiceStarted",
                sessionMaintainence?.getBoolean(SessionMaintainence.ISSERVICESTARTED).toString()
            )
            if (fromPush)
                addFloatingMenu()
            if (sessionM.getBoolean(SessionMaintainence.DriverStatus, false)) {
                Log.e("FloatingService", "excecuter status ${excecuter.isShutdown}")
//                if(excecuter.isShutdown)
                excecuter.scheduleAtFixedRate({
                    /*  if (isCalled > 6) {
                          locationUpdate()
                          isCalled = 0
                      } else isCalled++*/
                    // addFloatingMenu()
                    locationUpdate()
                }, 0, 20, TimeUnit.SECONDS)
            } else {
                removeFloatingContro()
                stopForeground(true)
                stopSelf()
                //  if (!excecuter.isShutdown)
                excecuter.shutdownNow()
            }
        }
//        if (!sessionM.getBoolean(SessionMaintainence.DriverStatus, false)) {
//            removeFloatingContro()
//            stopForeground(true)
//            stopSelf()
//        } else {
//            //  if (MyApplication.inBackground)
//            addFloatingMenu()
//            excecuter.scheduleAtFixedRate({
//                locationUpdate()
//            }, 2, 10, TimeUnit.SECONDS)
//
//        }
        return START_STICKY

        //Normal Service To test sample service comment the above    generateForegroundNotification() && return START_STICKY
        // Uncomment below return statement And run the app.
//        return START_NOT_STICKY
    }

    fun removeFloatingContro() {
        if (MyApplication.floatingControlView?.parent != null) {
            windowManager?.removeView(MyApplication.floatingControlView)
        }
    }

    private fun addFloatingMenu() {
        if (MyApplication.floatingControlView == null) {
            val li = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            MyApplication.floatingControlView =
                li.inflate(R.layout.layout_floating_control_view, null) as ViewGroup?
            //Set layout params to display the controls over any screen.
            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_PHONE else WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )
            params.height = dpToPx(50)
            params.width = dpToPx(50)
            iconWidth = params.width
            iconHeight = params.height
            screenHeight = windowManager?.defaultDisplay?.height ?: 0
            screenWidth = windowManager?.defaultDisplay?.width ?: 0
            //Initial position of the floating controls
            params.gravity = Gravity.TOP or Gravity.START
            params.x = 0
            params.y = 100
            windowManager?.addView(MyApplication.floatingControlView, params)
            try {
                addOnTouchListener(params)
                MyApplication.floatingControlView!!.setOnClickListener(View.OnClickListener {
                    Log.e("click---", "___")
                })
            } catch (e: Exception) {
                // TODO: handle exception
            }
        }
    }


    private fun openActivity() {
        val intent = Intent(this, DrawerActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent);
    }


    //Notififcation for ON-going
    private var iconNotification: Bitmap? = null

    //    private var notification: Notification? = null
    var mNotificationManager: NotificationManager? = null
    private val mNotificationId = 123

    private fun generateForegroundNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intentMainLanding = Intent(this, DrawerActivity::class.java)
            val intenttype =
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R)
                    PendingIntent.FLAG_IMMUTABLE
                else PendingIntent.FLAG_UPDATE_CURRENT

//            val pendingIntent =
//                PendingIntent.getActivity(this, 0, intenttype, 0)

            val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
                // Add the intent, which inflates the back stack
                addNextIntentWithParentStack(intentMainLanding)
                // Get the PendingIntent containing the entire back stack
                getPendingIntent(0, intenttype)
            }

            iconNotification =
                BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
            if (mNotificationManager == null) {
                mNotificationManager =
                    this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                assert(mNotificationManager != null)
                mNotificationManager?.createNotificationChannelGroup(
                    NotificationChannelGroup("chats_group", "Chats")
                )
                val notificationChannel =
                    NotificationChannel(
                        "service_channel", "Service Notifications",
                        NotificationManager.IMPORTANCE_HIGH
                    )
                notificationChannel.enableLights(false)
                notificationChannel.lockscreenVisibility = Notification.VISIBILITY_SECRET
                mNotificationManager?.createNotificationChannel(notificationChannel)
            }
            val builder = NotificationCompat.Builder(this, "service_channel")

            builder.setContentTitle(
                StringBuilder(resources.getString(R.string.app_name)).append(" service is running")
                    .toString()
            )
                .setTicker(
                    StringBuilder(resources.getString(R.string.app_name)).append("service is running")
                        .toString()
                )
                .setContentText("Touch to open") //                    , swipe down for more options.
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setWhen(0)
                .setOnlyAlertOnce(true)
                .setContentIntent(resultPendingIntent)
                .setOngoing(true)
            if (iconNotification != null) {
                builder.setLargeIcon(
                    Bitmap.createScaledBitmap(
                        iconNotification!!,
                        128,
                        128,
                        false
                    )
                )
            }
            builder.color = resources.getColor(R.color.purple_200)
            MyApplication.notification = builder.build()
            startForeground(mNotificationId, MyApplication.notification)
        }

    }

    private val getBubbleState: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val bubbleState = intent.getBooleanExtra(Config.BUBBLE_STATE, true)
            if (bubbleState) {
                if (Settings.canDrawOverlays(this@FloatingService))
                    if (!isAdded) {
                        addFloatingMenu()
                    } else removeFloatingContro()
            } else {
                if (MyApplication.floatingControlView != null) {
                    removeFloatingContro()
                    fromPush = false
                    MyApplication.floatingControlView = null
                    isAdded = false
                }
            }

        }
    }

    //Method to convert dp to px
    private fun dpToPx(dp: Int): Int {
        val displayMetrics = this.resources.displayMetrics
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
    }

    override fun onDestroy() {
        super.onDestroy()
        // excecuter.shutdownNow()
        isServiceStart = false
        // stopForeground()

        context?.let { LocalBroadcastManager.getInstance(it).unregisterReceiver(clearDist) }

        sessionMaintainence?.saveBoolean(SessionMaintainence.ISSERVICESTARTED, false)

        if (sessionM.getBoolean(SessionMaintainence.DriverStatus, false)) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(Intent(this, FloatingService::class.java))
            } else startService(Intent(this, FloatingService::class.java))

            val periodicTask =
                PeriodicWorkRequest.Builder(MyWorkerClass::class.java, 15, TimeUnit.MINUTES)
                    .setConstraints(Constraints.NONE)
                    .build()
            WorkManager.getInstance(this).enqueue(periodicTask)
        }

        //  stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    var location: Location? = null
    private fun locationUpdate() {
        var database = FirebaseDatabase.getInstance()
        ref = database.getReference("drivers")
        requestRef = database.getReference("requests")

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(context!!)
        if (ActivityCompat.checkSelfPermission(
                context!!, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                context!!, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        /* saving location to session */
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                Log.e(
                    "FloatingService",
                    "Obtained from fusedLocation" + location.latitude + "," + location.longitude
                )
                sessionM.edit().apply {
                    putString(SessionMaintainence.CURRENT_LATITUDE, "" + location.latitude)
                    apply()
                }
                sessionM.edit().apply {
                    putString(SessionMaintainence.CURRENT_LONGITUDE, "" + location.longitude)
                    apply()
                }
                sessionM.edit().apply {
                    putString(SessionMaintainence.BEARING, "" + location.bearing)
                    apply()
                }
            }
        }

        /* location update to firebase driver and request node */
        doWithLocation()
        Log.e(
            "FloatingService",
            "Saved dist :" + sessionM.getString(SessionMaintainence.SAVED_DISTANCE_TRAVELLED, "")
        )
        Log.e(
            "FloatingService",
            "Saved lat :" + sessionM.getString(SessionMaintainence.SAVED_TRIP_LAT, "")
        )
        Log.e(
            "FloatingService",
            "Saved lng :" + sessionM.getString(SessionMaintainence.SAVED_TRIP_LNG, "")
        )
        Log.e("FloatingService", "total dist :$totalDistanceTravelled")
    }


    private fun doWithLocation() {
        try {
            sessionM.getString(SessionMaintainence.CURRENT_LATITUDE, "")?.let { lat ->
                sessionM.getString(SessionMaintainence.CURRENT_LONGITUDE, "")?.let { lng ->
                    if (lat.isNotEmpty() && lng.isNotEmpty()) {
                        sessionM.getString(SessionMaintainence.DriverData, "")?.let { driverData ->
                            if (driverData.isNotEmpty()) {
                                val driverDataaa = Gson().fromJson(
                                    driverData, BaseResponse.ReqDriverData::class.java
                                )
                                val geoHash = GeoHash(GeoLocation(lat.toDouble(), lng.toDouble()))
                                val geoData = HashMap<String, Any>()
                                val driverData = HashMap<String, Any>()
                                sessionM.getString(SessionMaintainence.BEARING, "")
                                    ?.let { bearing ->
                                        if (bearing.isNotEmpty()) {
                                            geoData["bearing"] = bearing.toFloat()
                                        }
                                    }
                                val auth = FirebaseAuth.getInstance().currentUser

                                /* Check if inside trip  */
                                sessionM.getString(SessionMaintainence.ReqID, "")?.let { reqId ->
                                    if (reqId.isEmpty()) {
                                        geoData["g"] = geoHash.geoHashString
                                        geoData["updated_at"] = ServerValue.TIMESTAMP
                                        geoData["is_available"] = true
                                        geoData["l"] = Arrays.asList(lat.toDouble(), lng.toDouble())
                                        if (driverDataaa != null)
                                            ref!!.child(driverDataaa.slug!!)
                                                .updateChildren(geoData).addOnCompleteListener {
                                                    if (it.isSuccessful)
                                                        Log.e(
                                                            "FloatingService",
                                                            "driver node is updated"
                                                        )
                                                    else
                                                        Log.e(
                                                            "FloatingService",
                                                            "driver node updation failed"
                                                        )

                                                }.addOnFailureListener {
                                                    Log.e("FloatingService", "${it.message}")
                                                }
                                        clearTimeAndDistanceAfterTripCompleted()
                                    } else {
                                        /* calculate distance only if trip started */
                                        if (sessionM.getString(
                                                SessionMaintainence.IS_TRIP_STARTED,
                                                ""
                                            )
                                                .equals("TRUE")
                                        ) {
                                            val location = Location("driver")
                                            location.latitude = lat.toDouble()
                                            location.longitude = lng.toDouble()
                                            validateIntervalForLocations(location)
                                        }

                                        val reqTripsData = Gson().fromJson(
                                            sessionM.getString(SessionMaintainence.ReqTripData, ""),
                                            BaseResponse.MetaData::class.java
                                        )

                                        if (reqTripsData != null) {
                                            driverData["l"] = listOf(lat.toDouble(), lng.toDouble())
                                            driverData["is_available"] = false
                                            driverData["updated_at"] = ServerValue.TIMESTAMP
                                            if (driverDataaa != null) {
                                                ref!!.child(driverDataaa.slug!!)
                                                    .updateChildren(driverData)
                                                    .addOnSuccessListener {
                                                        Log.e(
                                                            "FloatingService",
                                                            "updatedValue - DriverSlug: ${driverDataaa.slug}"
                                                        )
                                                    }.addOnFailureListener { e ->
                                                        Log.e(
                                                            "FloatingService",
                                                            "onFailure Driver node update" + e.message.toString()
                                                        )
                                                    }
                                            }
                                            geoData["lat"] = lat.toDouble()
                                            geoData["lng"] = lng.toDouble()
                                            geoData["id"] = driverDataaa?.slug ?: ""
                                            geoData["user_id"] = reqTripsData.data?.user?.id ?: ""
                                            geoData["distancee"] =
                                                sessionM.getString(
                                                    SessionMaintainence.SAVED_DISTANCE_TRAVELLED,
                                                    ""
                                                )!!
                                            geoData["request_id"] = reqId
                                            requestRef!!.child(reqId).updateChildren(geoData)
                                                .addOnSuccessListener {
                                                    Log.e(
                                                        "FloatingService",
                                                        "updated request node ${reqId}"
                                                    )
                                                }.addOnFailureListener { e ->
                                                    Log.e(
                                                        "FloatingService",
                                                        "failed to update reqNode ${reqId}" + e.message.toString()
                                                    )
                                                }
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
            }

        } catch (e: NumberFormatException) {
            e.printStackTrace()
            Log.e("FloatingService", "Number format exception in location update")
        }
    }

    private fun addOnTouchListener(params: WindowManager.LayoutParams) {


        //Add touch listerner to floating controls view to move/close/expand the controls
        MyApplication.floatingControlView?.setOnTouchListener(object : View.OnTouchListener {
            private var initialTouchX = 0f
            private var initialTouchY = 0f
            private var initialX = 0
            private var initialY = 0

            override fun onTouch(view: View?, motionevent: MotionEvent): Boolean {
                val flag3: Boolean
                flag3 = true
                var flag = false
                when (motionevent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        currentTime = System.currentTimeMillis()
                        params.alpha = 1.0f
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = motionevent.rawX
                        initialTouchY = motionevent.rawY
                        // openActivity()
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        removedTime = System.currentTimeMillis()

                        timeDifference = removedTime - currentTime
                        /**
                         * 1 second delay for touch to open the bubble.
                         */
                        if (timeDifference < 100)
                            openActivity()
                        flag = flag3
                        if (Math.abs(initialTouchX - motionevent.rawX) >= 25f) {
                            return flag
                        } else {
                            flag = flag3
                            if (Math.abs(
                                    initialTouchY
                                            - motionevent.rawY
                                ) >= 25f
                            ) {
                                return flag
                            } else {
                                return true
                            }
                        }
                    }


                    MotionEvent.ACTION_MOVE -> {
                        initialX = params.x
                        initialY = params.y

                        if ((motionevent.rawX < (initialX - iconWidth / 2).toFloat()) || (motionevent.rawY < (initialY - iconHeight / 2).toFloat()) || (motionevent.rawX
                                .toDouble() > initialX.toDouble() + iconWidth.toDouble() * 1.2)
                        ) {
                        }
                        params.x = (motionevent.rawX - (iconWidth / 2).toFloat()).toInt()
                        params.y = (motionevent.rawY - iconHeight.toFloat()).toInt()
                        try {
                            windowManager?.updateViewLayout(
                                MyApplication.floatingControlView,
                                params
                            )
                        } // Misplaced declaration of an exception
                        // variable
                        catch (e: java.lang.Exception) {
                            e.printStackTrace()
//                                ExceptionHandling(e)
                            return true
                        }
                        return true
                    }
                    else -> {
                    }
                }
                return flag
            }
        })


    }

    fun clearTimeAndDistanceAfterTripCompleted() {
        totalDistanceTravelled = 0.0
        previousLatLng = null
        locationArray = JSONArray()
        offlineLocationArray = JSONArray()
        sessionM.edit().apply {
            putBoolean(SessionMaintainence.IS_GRACE_TIME_COMPLETED, false)
            apply()
        }
        sessionM.edit().apply {
            putBoolean(SessionMaintainence.IS_GRACE_AFTER_START_COMPLETED, false)
            apply()
        }
        sessionM.edit().apply {
            putInt(SessionMaintainence.SAVED_WAITING_TIME, 0)
            apply()
        }
        sessionM.edit().apply {
            putInt(SessionMaintainence.SAVED_GRACE_TIME, 0)
            apply()
        }
        sessionM.edit().apply {
            putInt(SessionMaintainence.SAVED_GRACE_TIME_AFTER_START, 0)
            apply()
        }
        sessionM.edit().apply {
            putString(SessionMaintainence.SAVED_DISTANCE_TRAVELLED, "")
            apply()
        }
        sessionM.edit().apply {
            putString(SessionMaintainence.IS_TRIP_STARTED, "FALSE")
            apply()
        }
        sessionM.edit().apply {
            putString(SessionMaintainence.SAVED_TRIP_LAT, "")
            apply()
        }
        sessionM.edit().apply {
            putString(SessionMaintainence.SAVED_TRIP_LNG, "")
            apply()
        }
        sessionM.edit().apply {
            putString(SessionMaintainence.DISPLAY_WAITING, "0.0")
            apply()
        }
    }


    private var lastUpdatedTime: Long = 0
    private fun validateIntervalForLocations(location: Location) {
        processLocation(location)
        if (System.currentTimeMillis() - lastUpdatedTime > 1000) {
            lastUpdatedTime = System.currentTimeMillis()
        }
    }

    private fun processLocation(location: Location) {
//        if (location.accuracy > 30)
        handleDistanceTravelled(location)
        //saveToLocationArray(location)
    }

    var previousLatLng: LatLng? = null
    var totalDistanceTravelled: Double =
        0.0

    private fun handleDistanceTravelled(location: Location) {
        assignPreviousValues()
        if (sessionM.getString(SessionMaintainence.IS_TRIP_STARTED, "")
                .equals("TRUE") && sessionM.getString(SessionMaintainence.ReqID, "")
                ?.trim()!!.isNotEmpty()
        ) {
            if (previousLatLng == null)
                previousLatLng = LatLng(location.latitude, location.longitude)
            else {
                totalDistanceTravelled += getDistanceFrom(
                    previousLatLng!!, LatLng(location.latitude, location.longitude)
                )
                previousLatLng = LatLng(location.latitude, location.longitude)
                sessionM.edit().apply {
                    putString(SessionMaintainence.SAVED_TRIP_LAT, "${location.latitude}")
                    apply()
                }
                sessionM.edit().apply {
                    putString(SessionMaintainence.SAVED_TRIP_LNG, "${location.longitude}")
                    apply()
                }
                sessionM.edit().apply {
                    putString(
                        SessionMaintainence.SAVED_DISTANCE_TRAVELLED,
                        "$totalDistanceTravelled"
                    )
                    apply()
                }
                val intent = Intent(Config.RECEIVE_DISTANCE)
                intent.putExtra("DISTANCE", totalDistanceTravelled)
                context
                    ?.let { LocalBroadcastManager.getInstance(it).sendBroadcast(intent) }
            }
        }
    }


    private fun assignPreviousValues() {
        if (sessionM.getString(SessionMaintainence.ReqID, "")?.isNotEmpty() == true) {
            if (sessionM.getString(SessionMaintainence.SAVED_TRIP_LAT, "")?.isNotEmpty() == true &&
                sessionM.getString(SessionMaintainence.SAVED_TRIP_LNG, "")?.isNotEmpty() == true
            ) {
                previousLatLng = LatLng(
                    sessionM.getString(SessionMaintainence.SAVED_TRIP_LAT, "")!!.toDouble(),
                    sessionM.getString(SessionMaintainence.SAVED_TRIP_LNG, "")!!.toDouble()
                )
            }
            if (sessionM.getString(SessionMaintainence.SAVED_DISTANCE_TRAVELLED, "")
                    ?.isNotEmpty() == true
            )
                totalDistanceTravelled =
                    sessionM.getString(SessionMaintainence.SAVED_DISTANCE_TRAVELLED, "")!!
                        .toDouble()

        }
    }

    private fun getDistanceFrom(latLng: LatLng, latLng2: LatLng): Double {
        val distance: Double
        var distanceComputed = 0.0
        try {
            val list: MutableList<LatLng> = java.util.ArrayList()
            list.add(latLng)
            list.add(latLng2)
            distance = SphericalUtil.computeLength(list)
            distanceComputed = distance / 1000 /*+((distance / 1000)*0.150)*/
            //            socketDataReceiver.updateTripDistance(distanceComputed);
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return distanceComputed
    }

    var locationArray = JSONArray()
    var offlineLocationArray = JSONArray()
    private fun saveToLocationArray(location: Location) {
        val currentTime = System.currentTimeMillis()
        /* adding offline data to locationArray */
        if (offlineLocationArray.length() > 0) {

            for (i in 0 until offlineLocationArray.length()) {
                val explrObject: JSONObject = offlineLocationArray.getJSONObject(i)
                locationArray.put(explrObject)
            }
            offlineLocationArray = JSONArray()
        }

        val obj = JSONObject()
        try {
            obj.put(Config.lat, location.latitude)
            obj.put(Config.lng, location.longitude)
            if (currentTime - lastUpdatedTime > 3000) {
                if (Utilz.checkForInternet(context!!)) {
                    locationArray.put(obj)
                    updateLocationArrayFireBase()
                } else {
                    offlineLocationArray.put(obj)
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private val jTripLocation: JSONObject = JSONObject()
    private fun updateLocationArrayFireBase() {
        if (sessionM.getString(SessionMaintainence.ReqID, "") != null && sessionM.getString(
                SessionMaintainence.ReqID,
                ""
            )!!.isNotEmpty() && locationArray.length() > 0
        ) {
            try {
                jTripLocation.put("lat_lng_array", locationArray)
                val result: HashMap<String, Any> = Gson().fromJson(
                    jTripLocation.toString(),
                    HashMap::class.java
                ) as HashMap<String, Any>
                requestRef?.child(sessionM.getString(SessionMaintainence.ReqID, "")!!)
                    ?.updateChildren(result)?.addOnCompleteListener {
                        Log.e(
                            "FloatingService",
                            "Location Array Length update - ${locationArray.length()}"
                        )
                    }
            } catch (e: java.lang.Exception) {
                Log.e("FloatingService", e.message ?: "")
            }

        }
    }


}
