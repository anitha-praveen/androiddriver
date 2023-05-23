package com.rodaClone.driver.ut

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.core.GeoHash
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.gson.Gson
import com.rodaClone.driver.R
import com.rodaClone.driver.connection.BaseResponse
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class LocationUpdateService : Service() {

    private val NOTIFICATION_CHANNEL_ID: String = "foregroundService"
    var ref: DatabaseReference? = null
    var requestRef: DatabaseReference? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var sessionM: SharedPreferences
    val id_mid = "id_mid"
    val jid_transl = "jid_transl"
    var context: Context? = null
    private var executoService: ScheduledExecutorService =
        Executors.newSingleThreadScheduledExecutor()

    companion object {
        var isServiceStarted = false
        var enterCount = 0
    }

    private var lastUpdatedTime: Long = 0
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        isServiceStarted = true
        if (context != null)
            context = this

        executoService.scheduleAtFixedRate({

            if (System.currentTimeMillis() - lastUpdatedTime > 5000) {
                lastUpdatedTime = System.currentTimeMillis()
                locationUpdate()
            }

//             LocalBroadcastManager.getInstance(this).sendBroadcast(Intent(Config.LOCAION_UPDATES))
        }, 2.toLong(), 10.toLong(), TimeUnit.SECONDS)

        // Timer().schedule(5000) {
        //do something
        // locationUpdate()
        //  }

        Log.e("enteringStartCommand--", "__")
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }


    override fun onCreate() {
        super.onCreate()
        //isServiceStarted = true
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setOngoing(false)
                .setContentTitle("App running in background")
                .setSmallIcon(R.mipmap.ic_launcher_round)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_ID, NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.description = NOTIFICATION_CHANNEL_ID
            notificationChannel.setSound(null, null)
            notificationManager.createNotificationChannel(notificationChannel)
            startForeground(1, builder.build())
        }

        context = this
        val keyGenParameterSpec=
            MasterKey.Builder(this).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
        sessionM = EncryptedSharedPreferences.create(this,Config.sharedPrefName,keyGenParameterSpec,EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)

    }

    override fun onDestroy() {
        super.onDestroy()
        executoService.shutdownNow()
        stopForeground(false)
        stopSelf()
    }

    private fun locationUpdate() {
        var database = FirebaseDatabase.getInstance()
        ref = database.getReference("drivers")
        requestRef = database.getReference("requests")

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(context!!)
        if (ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                if (sessionM.getString(SessionMaintainence.DriverData, "")!!.isNotEmpty()) {
                    val driverDataaa = Gson().fromJson(
                        sessionM.getString(SessionMaintainence.DriverData, ""),
                        BaseResponse.ReqDriverData::class.java
                    )
                    Log.e(
                        "LocUpdate",
                        "locationVal--" + location.latitude + driverDataaa.phoneNumber
                    )
//                newLocation = location
//                if (oldLocation == null)
//                    oldLocation = newLocation

//                sessionM.saveString(SessionMaintainence.CURRENT_LATITUDE, "" + location.latitude)
//                sessionM.saveString(SessionMaintainence.CURRENT_LONGITUDE, "" + location.longitude)
                    val geoHash = GeoHash(GeoLocation(location.latitude, location.longitude))
                    val geoData = HashMap<String, Any>()


//                geoData["bearing"] = newLocation!!.bearingTo(oldLocation)
                    geoData["bearing"] = location.bearing
                    val auth = FirebaseAuth.getInstance().currentUser

                    if (sessionM.getString(SessionMaintainence.ReqID, "").isNullOrEmpty()) {
                        // printE("entryValuee---", "___entering")
                        geoData["g"] = geoHash.geoHashString
                        geoData["updated_at"] = ServerValue.TIMESTAMP
                        geoData["is_available"] = true
                        geoData["l"] = Arrays.asList(location.latitude, location.longitude)
                        if (driverDataaa != null)
                            ref!!.child(driverDataaa.slug!!)
                                .updateChildren(geoData) { error, ref ->
                                }
                        //  clearTimeAndDistanceAfterTripCompleted()
                    } else {
                        Log.e(
                            "LocUpdate",
                            "Requsttt"
                        )
                        val reqTripsData = Gson().fromJson(
                            sessionM.getString(SessionMaintainence.ReqTripData, ""),
                            BaseResponse.MetaData::class.java
                        )
                        if (reqTripsData != null) {
                            geoData["lat"] = location.latitude
                            geoData["lng"] = location.longitude
                            geoData["id"] = driverDataaa?.slug ?: ""
                            geoData["user_id"] = reqTripsData?.data!!.user?.id ?: ""
                            geoData["request_id"] =
                                sessionM.getString(SessionMaintainence.ReqID, "") ?: ""

                            requestRef!!.child(
                                sessionM.getString(SessionMaintainence.ReqID, "") ?: ""
                            ).updateChildren(geoData)
                        }
                    }
                }

            }
        }
    }

}