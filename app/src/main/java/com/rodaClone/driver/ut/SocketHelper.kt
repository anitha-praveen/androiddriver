package com.rodaClone.driver.ut

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import io.socket.engineio.client.transports.WebSocket
import org.json.JSONException
import org.json.JSONObject
import java.net.URISyntaxException

class SocketHelper {

    companion object {
        var TAG = "SocketHelper"

        var lastSocketConnected: Long? = null
        var lastLocationUpdate: kotlin.Long? = null

        private val database = FirebaseDatabase.getInstance()
        private val ref = database.getReference("drivers")
        private val requestRef = database.getReference("requests")
        private val LocationUpdateMinimumTime = 5000
        private val locationUpdateSocketServerMinimumTime = 12000

        var mSocket: Socket? = null
        private var socketDataReceiver: SocketListener? = null
        var sharedPrefence: SessionMaintainence? = null
        fun init(
            prefence: SessionMaintainence?,
            socketDataReceivers: SocketListener?
        ) {
            socketDataReceiver = socketDataReceivers
            sharedPrefence = prefence
            //        TAG = tag;
            SetSocketListener()
        }

        fun SetSocketListener() {
            val opts = IO.Options()
            opts.forceNew = true
            opts.reconnection = true
            opts.transports = arrayOf(WebSocket.NAME)
            try {
                if (mSocket == null) mSocket = IO.socket(Config.SOCKET_URL, opts)
                 if (!mSocket!!.connected()) {
                     Log.v(
                         "SocketTriggering",
                         "xxxxxxxxxxxxxxxxxxxxx" + if (mSocket != null) "Is connected" + mSocket!!.connected() else "mSocket is Null"
                     )
                 }
                mSocket!!.on(Socket.EVENT_CONNECT, onConnect)
                mSocket!!.on(Socket.EVENT_DISCONNECT, onDisconnect)
                mSocket!!.on(Socket.EVENT_CONNECT_ERROR, onConnectError)
                mSocket!!.on(
                    Config.DRIVERREQUEST + sharedPrefence!!.getString(
                        SessionMaintainence.DriverID
                    ), onGetRequest
                )
                mSocket!!.on(
                    Config.locationchanged_ + sharedPrefence!!.getString(
                        SessionMaintainence.DriverID
                    ), onLocationChanged
                )
                mSocket!!.on(
                    Config.package_changed_ + sharedPrefence!!.getString(
                        SessionMaintainence.DriverID
                    ), onPackageChanged
                )
                mSocket!!.on(
                    Config.photo_upload_ + sharedPrefence!!.getString(
                        SessionMaintainence.DriverID
                    ), onPhotoUpload
                )

                mSocket!!.connect()

            } catch (e: URISyntaxException) {
                e.printStackTrace()
            }
        }

        fun disConnectSocket() {
            if (mSocket == null) return
            /**********Trunning Off Socket */
            val `object` = JSONObject()
            try {
                `object`.put(
                    Config.id,
                    sharedPrefence!!.getString(SessionMaintainence.DriverID)
                )
                `object`.put("socket_id", mSocket!!.id() + "")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            mSocket!!.disconnect()
            mSocket!!.off(Socket.EVENT_CONNECT, onConnect)
            mSocket!!.off(Socket.EVENT_DISCONNECT, onDisconnect)
            mSocket!!.off(Socket.EVENT_CONNECT_ERROR, onConnectError)
            mSocket!!.off(
                Config.DRIVERREQUEST + sharedPrefence!!.getString(
                    SessionMaintainence.DriverID
                ), onGetRequest
            )
            mSocket!!.off(
                Config.locationchanged_ + sharedPrefence!!.getString(
                    SessionMaintainence.DriverID
                ), onLocationChanged
            )
            mSocket!!.off(
                Config.package_changed_ + sharedPrefence!!.getString(
                    SessionMaintainence.DriverID
                ), onPackageChanged
            )
            mSocket!!.off(
                Config.photo_upload_ + sharedPrefence!!.getString(
                    SessionMaintainence.DriverID
                ), onPhotoUpload
            )
        }

        private val onConnect = Emitter.Listener {
            Log.i(TAG, "connected")
            val `object` = JSONObject()
            try {
                `object`.put(
                    Config.id,
                    sharedPrefence!!.getString(SessionMaintainence.DriverID)
                )
                //  `object`.put("socket_id", mSocket!!.id() + "")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            if (socketDataReceiver != null && socketDataReceiver!!.isNetworkConnected()) {
                mSocket!!.emit(
                    Config.start_connect,
                    `object`.toString()
                )
            }
        }

        private val onDisconnect = Emitter.Listener {
//            Log.i(TAG, "onDisconnect")
            if (socketDataReceiver != null) socketDataReceiver!!.OnDisconnect()
            lastSocketConnected = null
        }

        private val onConnectError = Emitter.Listener {
//            Log.i(TAG, "onConnectError")
            if (socketDataReceiver != null) socketDataReceiver!!.OnConnectError()
            lastSocketConnected = null
        }

        private val onGetRequest =
            Emitter.Listener { args ->
                if (socketDataReceiver != null && args != null)
                    socketDataReceiver!!.getRequest(args[0].toString())
            }

        private val onLocationChanged =
            Emitter.Listener { args ->
                if (socketDataReceiver != null && args != null)
                    socketDataReceiver!!.locationChanged(args[0].toString())
            }

        private val onPackageChanged =
            Emitter.Listener { args ->
                if(socketDataReceiver != null && args != null)
                    socketDataReceiver!!.packageChanged(args[0].toString())
            }

        private val onPhotoUpload =
            Emitter.Listener { args ->
                if(socketDataReceiver != null && args != null){
                    socketDataReceiver!!.photoUploaded(args[0].toString())
                    Log.e("onPhotoUpload","Socket"+args[0].toString())
                }
            }

        interface SocketListener {
            fun OnConnect()
            fun OnDisconnect()
            fun OnConnectError()
            fun getRequest(data: String)
            fun isNetworkConnected(): Boolean
            fun locationChanged(data: String)
            fun packageChanged(data: String)
            fun photoUploaded(data: String)
        }

    }


}