package com.rodaClone.driver.ut

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.work.Constraints
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.rodaClone.driver.MyApplication
import com.rodaClone.driver.R
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.drawer.DrawerActivity
import com.rodaClone.driver.drawer.trip.TripObject
import com.rodaClone.driver.splash.SplashActivity
import com.rodaClone.driver.transparentAct.TranspActivity
import java.util.*
import java.util.concurrent.TimeUnit


class MyFirebaseMessagingService : FirebaseMessagingService() {

    lateinit var sessionM: SharedPreferences
    private var ref: DatabaseReference? = null
    var requestRef: DatabaseReference? = null

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */

    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val keyGenParameterSpec=
            MasterKey.Builder(this).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
        sessionM = EncryptedSharedPreferences.create(this,Config.sharedPrefName,keyGenParameterSpec,EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)


//        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
//        val masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)
//        sessionM = EncryptedSharedPreferences.create(
//            "276966796008290",
//            masterKeyAlias, this,
//            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
//            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
//        )

        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: ${remoteMessage.data}")
        val baseResponse: BaseResponse? =
            Utilz.getSingleObject(remoteMessage.data["body"], BaseResponse::class.java)
        when (baseResponse?.notificationEnum) {
            null -> {
                Log.e(TAG, "onMessageReceived: drawer activity  ${DrawerActivity.isDrawer}", )
                notifyStartService()
                remoteMessage.data["title"]?.let { sendNotification(it) }
                wakupScreen(MyApplication.appContext!!)
            }
            "request_created" -> {
                if(DrawerActivity.isDrawer){
                    openDrawerAct(remoteMessage.data["title"] ?: "New request")
                }else{
                    LocalBroadcastManager.getInstance(this).sendBroadcast(Intent(Config.NEW_REQUEST))
                }
                sendNotification(remoteMessage.data["title"] ?: "Notification")

            }
            "logout_push" -> {
                updatDriverStatus()
            }
            "silent_push" -> {
                notifyStartService()
                wakupScreen(MyApplication.appContext!!)
                sendNotification(remoteMessage.data["title"] ?: "Notification")
            }

            "driver_blocked" -> {
                if (!DrawerActivity.isDrawer)
                    OpenApprovalFrag()
                sendNotification(remoteMessage.data["title"] ?: "Notification")
            }
            "driver_approved" -> {
                if (!DrawerActivity.isDrawer)
                    OpenApprovalFrag()
                sendNotification(remoteMessage.data["title"] ?: "Notification")
            }
            "request_cancelled_by_user" -> {
                if (!DrawerActivity.isDrawer) {
                    val intent = Intent(Config.CLOSE_ACCEPTREJECT)
                    LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
                }
                sendNotification(remoteMessage.data["title"] ?: "Notification")
            }
            "local_to_rental" -> {
                val intent = Intent(Config.LOC_TO_RENT_PUSH)
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
                sendNotification(remoteMessage.data["title"] ?: "Notification")
            }
            "passenger_upload_images" ->{
                val intent = Intent(Config.PUSH_UPLOAD_NIGHT)
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
                sendNotification(remoteMessage.data["title"] ?: "Notification")
            }
            else -> {

                sendNotification(remoteMessage.data["title"] ?: "Notification")
            }
        }

//        showCustom()
    }

    private fun updatDriverStatus() {
        var database = FirebaseDatabase.getInstance()
        ref = database.getReference("drivers")

        requestRef = database.getReference("requests")
        val session = SessionMaintainence(this)
        val slug = session.getString(SessionMaintainence.DriverID)

        val driverDataMap: MutableMap<String, Any?> = HashMap()
        driverDataMap["is_active"] = "false"

        ref!!.child(slug!!).updateChildren(driverDataMap)
        session.saveBoolean(SessionMaintainence.DriverStatus, false)
        if (!DrawerActivity.isDrawer) {
            val intent = Intent(Config.LOGOUT_RECEIVER)
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        }
    }

    // [START on_new_token]
    /**
     * Called if the FCM registration token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the
     * FCM registration token is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
//        Log.d(TAG, "Refreshed token: $token")
        sendRegistrationToServer(token)
    }
    // [END on_new_token]


    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private fun handleNow() {
//        Log.d(TAG, "Short lived task is done.")
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private fun sendRegistrationToServer(token: String?) {
        // TODO: Implement this method to send token to your app server.
//        Log.d(TAG, "sendRegistrationTokenToServer($token)")
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private fun sendNotification(messageBody: String) {
        val intent = Intent(this, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val intenttype =
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R)
                PendingIntent.FLAG_IMMUTABLE
            else PendingIntent.FLAG_UPDATE_CURRENT
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            intenttype
        )

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(messageBody)
            .setStyle(NotificationCompat.BigTextStyle().bigText(messageBody))
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }


    var channelId: String? = null
    var alarmSound: Uri? = null
    private fun openDrawerAct(title: String) {
        val resultIntent = Intent(this, DrawerActivity::class.java)
//        resultIntent.putExtra(Config.REQ_DATA, Gson().toJson(data))

        // Create the TaskStackBuilder
        val intenttype =
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R)
                PendingIntent.FLAG_IMMUTABLE
            else PendingIntent.FLAG_UPDATE_CURRENT

        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            // Add the intent, which inflates the back stack
            addNextIntentWithParentStack(resultIntent)
            // Get the PendingIntent containing the entire back stack
            getPendingIntent(0, intenttype)
        }

//        val channelId = "${System.currentTimeMillis()}"
//        val defaultSoundUri: Uri =
//            Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + packageName + "/" + R.raw.rodataxi)
//
//
//        val notificationBuilder = NotificationCompat.Builder(this, channelId)
//            .setSmallIcon(R.mipmap.road_logo_round)
//            .setContentTitle(getString(R.string.app_name))
//            .setContentText(title)
//            .setAutoCancel(true)
//            .setSound(defaultSoundUri)
//            .setContentIntent(resultPendingIntent)
//
//        createNotificationChannel(channelId)
//        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())


        Log.e("check1", "2")
        val r = Random()
        channelId = (Random().nextInt(1000000 - 10 + 1) + 10).toString()


        alarmSound =
            Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + packageName + "/" + R.raw.new_request)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(
                channelId,
                MyApplication.appContext?.getString(R.string.default_notification_channel_id),
                importance
            )
            mChannel.name = title
            mChannel.description = title
            mChannel.enableLights(true)
            mChannel.lightColor = Color.RED
            mChannel.enableVibration(false)
            mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            mChannel.lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()
            mChannel.setSound(alarmSound, audioAttributes)
            notificationManager.createNotificationChannel(mChannel)
        }


        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(MyApplication.appContext!!, channelId!!)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(MyApplication.appContext!!.getString(R.string.app_name))
                .setContentText(title)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(title)
                )
                .setSound(alarmSound)
                .setContentIntent(resultPendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        notificationManager.notify(10, notificationBuilder.build())
        wakupScreen(MyApplication.appContext!!)

        val i = Intent(this, DrawerActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(i)

    }

    private fun OpenApprovalFrag() {
        val i = Intent(this, DrawerActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(i)
    }

    fun wakupScreen(context: Context) {
        val pm = context.getSystemService(POWER_SERVICE) as PowerManager
        val isScreenOn = pm.isScreenOn
        if (!isScreenOn) {
            val wl = pm.newWakeLock(
                PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE,
                Config.WAKE_LOCK_TAG
            )
            wl.acquire(10000)
            val wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, Config.WAKE_LOCK_TAG2)
            wl_cpu.acquire(10000)
        }
    }

    companion object {

        private const val TAG = "MyFirebaseMsgService"

    }

    //check starting service
    private fun notifyStartService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent(this, FloatingService::class.java)
            intent.putExtra("fromPush", "FromPush")
            startForegroundService(intent)
        } else startService(Intent(this, FloatingService::class.java))
        val periodicTask =
            PeriodicWorkRequest.Builder(MyWorkerClass::class.java, 15, TimeUnit.MINUTES)
                .setConstraints(Constraints.NONE)
                .build()
        WorkManager.getInstance(this).enqueue(periodicTask)
    }

    private fun showCustom(){
        val i = Intent(this, TranspActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(i)

    }

}