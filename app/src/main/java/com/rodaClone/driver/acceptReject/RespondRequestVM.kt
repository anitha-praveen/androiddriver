package com.rodaClone.driver.acceptReject

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import androidx.core.app.NotificationManagerCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.google.gson.Gson
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseVM
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.connection.ConnectionHelper
import com.rodaClone.driver.connection.CustomException
import com.rodaClone.driver.connection.responseModels.RequestResponseData
import com.rodaClone.driver.drawer.trip.TripObject
import com.rodaClone.driver.ut.Config
import com.rodaClone.driver.ut.SessionMaintainence
import retrofit2.Call
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

class RespondRequestVM @Inject constructor(
    val session: SessionMaintainence,
    val mConnect: ConnectionHelper

) :
    BaseVM<BaseResponse, RespondRequestNav>(session, mConnect) {
    var isScheduledTrip = ObservableBoolean(true)
    var pickupAddr = ObservableField("")
    var dropAddr = ObservableField("")
    var CurrentTimeTicking = ObservableField("")
    var timeLeft = ObservableField("0")
    var userName = ObservableField("")
    var ratingString = ObservableField("")
    var reqId = ObservableField("")
    var serviceCategory = ObservableField("")
    val isStopsAvailable = ObservableBoolean(false)
    val gson = Gson()
    val isMultidropAdded = ObservableBoolean(false)
    private var map: HashMap<String, String> = HashMap()
    var mMediaPlayer: MediaPlayer? = null
    val multiDropAddress = ObservableField("")
    val multiDropLat = ObservableField("")
    val multiDropLong = ObservableField("")
    var tripType = ObservableField("Local")
    var showDropAddress = ObservableBoolean(false)
    val showNotes = ObservableBoolean(false)
    val userNotes = ObservableField("")
    val tripStartTime = ObservableField("")
    val tripEndTime = ObservableField("")
    override fun onSuccessfulApi(response: BaseResponse?) {
        stopSound()
        mainHandler.removeCallbacks(progressRunner)
        isLoading.value = false
        NotificationManagerCompat.from(getNavigator().getCtx()).cancelAll()
        getNavigator().callReqProgress()
    }

    override fun onFailureApi(call: Call<BaseResponse?>?, t: CustomException?) {
        mainHandler.removeCallbacks(progressRunner)
        stopSound()
        isLoading.value = false
        getNavigator().showMessage(t!!.exception!!)
        getNavigator().callReqProgress()
    }

    /**
     * Get the trip info from request progress or Socket
     */
    var progressRate = ObservableField(0)
    val mainHandler = Handler(Looper.getMainLooper())
    val progressRunner = object : Runnable {
        override fun run() {
            progressRate.set(progressRate.get()?.plus(1))
            if ((maxTime.get()!! - progressRate.get()!!) > 0)
                CurrentTimeTicking.set("${maxTime.get()!! - progressRate.get()!!}")
            if (maxTime.get() != 0 && maxTime.get()!! <= progressRate.get()!!) {
                acceptRejectClicked(0)
                stopSound()
            }

            mainHandler.postDelayed(this, 1000)
        }
    }

    var maxTime = ObservableField(0)
    fun requestDetails(responseData: RequestResponseData) {
//        listener = this

        // no notification sound
        if (TripObject.isAcceptRejectVisible)
            responseData.id?.let { session.saveString(SessionMaintainence.LAST_REQ_ID, it) }
        showDropAddress.set(!responseData.serviceCategory.equals("RENTAL", true))
        pickupAddr.set(responseData.pickAddress)
        dropAddr.set(responseData.dropAddress)
        isScheduledTrip.set(responseData.isLater == 1)
        serviceCategory.set(responseData.serviceCategory)
        val pkg = when {
            responseData.serviceCategory?.equals(
                "RENTAL",
                true
            ) == true -> "${responseData.packageHour}hr and ${responseData.packageKm}km"
            responseData.serviceCategory?.equals("OUTSTATION", true) == true -> "- ${
                if (responseData.outstationTripType.equals("TWO", true))
                    translationModel?.txt_two_way
                else translationModel?.txt_one_way
            }"
            else -> ""
        }
        tripType.set(responseData.serviceCategory + " ${pkg.uppercase()}")
        userName.set(responseData.user!!.firstname)
        ratingString.set("5")
        responseData.driver_notes.let {
            if (it.isNullOrEmpty())
                showNotes.set(false)
            else {
                showNotes.set(true)
                userNotes.set("${translationModel?.txt_user_notes}: ${it}")
            }
        }
        reqId.set(responseData.id)
        try {
            responseData.driverTimeOut?.toInt()?.let {
                getNavigator().setMax(it)
                maxTime.set(it)
            }
            mainHandler.post(progressRunner)
        } catch (nfe: NumberFormatException) {
            // not a valid int
        }
        timeLeft.set(responseData.driverTimeOut)
        multiDropAddress.set(responseData.stops?.address ?: "")
        multiDropLat.set(responseData.stops?.latitude ?: "")
        multiDropLong.set(responseData.stops?.longitude ?: "")
        if (multiDropAddress.get().isNullOrEmpty())
            isMultidropAdded.set(false)
        else
            isMultidropAdded.set(true)

        responseData.tripStartTime?.let {
            try {
                val mDate =
                    SimpleDateFormat("dd-MM-yyy HH:mm:ss", Locale.ENGLISH).parse(it)
                if (mDate != null) {
                    tripStartTime.set(
                        translationModel?.txt_start_date + " : " + SimpleDateFormat(
                            "dd-MM-yyyy",
                            Locale.ENGLISH
                        ).format(mDate)
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                tripStartTime.set(it)
            }
        }

        responseData.tripEndtime?.let {
            try {
                val mDate =
                    SimpleDateFormat("dd-MM-yyy HH:mm:ss", Locale.ENGLISH).parse(it)
                if (mDate != null) {
                    tripEndTime.set(
                        translationModel?.txt_end_date + " : " + SimpleDateFormat(
                            "dd-MM-yyyy",
                            Locale.ENGLISH
                        ).format(mDate)
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }


    fun playSound() {
        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer.create(getNavigator().getCtx(), R.raw.beep)
            mMediaPlayer?.isLooping = true
            mMediaPlayer?.start()
        } else {
            if (mMediaPlayer?.isPlaying == true) {
                stopSound()
                mMediaPlayer?.start()
            } else {
                mMediaPlayer?.start()
            }
        }
    }

    fun stopSound() {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.stop()
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
    }

    fun acceptRejectClicked(clickValue: Int) {
        if (getNavigator().isNetworkConnected()) {
            map.clear()
            map["request_id"] = reqId.get()!!
            map["is_accept"] = "" + clickValue
            map[Config.DRIVER_LAT] = session.getString(SessionMaintainence.CURRENT_LATITUDE)!!
            map[Config.DRIVER_LNG] = session.getString(SessionMaintainence.CURRENT_LONGITUDE)!!
            respondApi(map)
        } else getNavigator().showNetworkUnAvailable()
    }

    fun onClikAccept() {
        acceptRejectClicked(1)
    }

}


@BindingAdapter("progress")
fun setProgress(p: ProgressBar, value: Int) {
    p.progress = value
}