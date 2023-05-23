package com.rodaClone.driver.transparentAct

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseVM
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.connection.ConnectionHelper
import com.rodaClone.driver.connection.CustomException
import com.rodaClone.driver.ut.Config
import com.rodaClone.driver.ut.SessionMaintainence
import retrofit2.Call
import javax.inject.Inject

class TransparentVM @Inject constructor(
    val session: SessionMaintainence,
    val mConnect: ConnectionHelper

) : BaseVM<BaseResponse, TransparentActNav>(session, mConnect) {
    override fun onSuccessfulApi(response: BaseResponse?) {
       isLoading.value = false
    }
    val tripTxt = ObservableField("")
    var reqId = ""
    val isStopsAvailable = ObservableBoolean(false)
    val pickupAddr = ObservableField("LMS Street, Coimbatore, Tamil Nadu")
    val isMultidropAdded = ObservableBoolean(false)
    val multiDropAddress = ObservableField("")
    val showDropAddress = ObservableBoolean(true)
    val dropAddr = ObservableField("Ukkadam, Coimbatore, Tamil Nadu")
    val tripType = ObservableField("Rental 1 hr 10 km")
    val tripStartTime = ObservableField("")
    val serviceCategory = ObservableField("RENTAL")
    val tripEndTime = ObservableField("")

    override fun onFailureApi(call: Call<BaseResponse?>?, t: CustomException?) {
        isLoading.value = false
    }

    fun onClickAccept(value : Int){
        if (getNavigator().isNetworkConnected()) {
            map.clear()
            map["request_id"] = reqId
            map["is_accept"] = "" + value
            map[Config.DRIVER_LAT] = session.getString(SessionMaintainence.CURRENT_LATITUDE)!!
            map[Config.DRIVER_LNG] = session.getString(SessionMaintainence.CURRENT_LONGITUDE)!!
//            respondApi(map)
        } else getNavigator().showNetworkUnAvailable()

    }

    private var map: HashMap<String, String> = HashMap()
    var maxTime = 15
    var progressRate = 0
    val currentTimeTicking = ObservableField("")
    val mainHandler = Handler(Looper.getMainLooper())
    val progressRunner = object : Runnable {
        override fun run() {
            progressRate += 1
            getNavigator().setProg(progressRate)
            if((maxTime - progressRate) > 0)
                currentTimeTicking.set("${maxTime - progressRate}")
            if(maxTime!=0 && maxTime <= progressRate){
                onClickAccept(0)
            }
            mainHandler.postDelayed(this, 1000)
        }
    }

    var mMediaPlayer: MediaPlayer? = null
    fun playSound() {
        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer.create(getNavigator().getCtx(), R.raw.new_request)
            mMediaPlayer?.isLooping = true
            mMediaPlayer?.start()
        } else {
            if(mMediaPlayer?.isPlaying==true){
                stopSound()
                mMediaPlayer?.start()
            }else{
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

}