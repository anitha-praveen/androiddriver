package com.rodaClone.driver.loginSignup.otp

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.CountDownTimer
import android.text.Editable
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.google.gson.Gson
import com.rodaClone.driver.R
import com.rodaClone.driver.base.BaseVM
import com.rodaClone.driver.connection.BaseResponse
import com.rodaClone.driver.connection.ConnectionHelper
import com.rodaClone.driver.connection.CustomException
import com.rodaClone.driver.ut.Config
import com.rodaClone.driver.ut.SessionMaintainence
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import java.util.HashMap
import javax.inject.Inject


class OtpVM @Inject constructor(
    val session: SessionMaintainence,
    val mConnect: ConnectionHelper

) :
    BaseVM<BaseResponse, OtpNavigator>(session, mConnect) {
    var phoneNumber = ObservableField("")
    var countryCode = ObservableField("")
    var countryCodeId = ObservableField("")
    var enableResend = ObservableBoolean(false)
    var isPrimary = ObservableBoolean(false)
    var map = HashMap<String, String>()
    var runnable = Runnable { enableResend.set(true) }
    var timerText = ObservableField("")
    var resendtimer = 120
    val showDidntRecive = ObservableBoolean(false)
    lateinit var data: BaseResponse.NewUser
    val gson = Gson()
    lateinit var countdown_timer: CountDownTimer
    var time_in_milli_seconds = 0L
    var mode = ObservableField(-1)
    var isOtpButtonEnabled = ObservableBoolean(true)
    var otp = ObservableField("")
    /*
    savedOTP is used when firebase auto-verified without user typed otp
     */
    var savedOtp = ""
    /*
   apiCode 0 -> token api
           1 -> loginOrSignUp
           2 -> update phone number
           3 -> otp api
    */
    var apiCode = -1
    var buttonText = ObservableField("")
    var displayText = ObservableField("")
    override fun onSuccessfulApi(response: BaseResponse?) {
        isLoading.value = false
        if (apiCode == 0 && response!!.accessToken != null) {
            session.saveString(SessionMaintainence.AccessToken, response.accessToken!!)
            session.saveString(SessionMaintainence.SelectedCountryCode, countryCode.get()!!)
            getNavigator().goToDrawer()
        } else if (apiCode == 1 && response!!.data != null) {
            data = gson.fromJson(Gson().toJson(response.data), BaseResponse.NewUser::class.java)
            if (data.newUser == true)
                getNavigator().goToSignUp()
            else {
                if (getNavigator().isNetworkConnected()) {
                    apiCode = 0
                    map.clear()
                    map[Config.grant_type] = "client_credentials"
                    map[Config.client_id] = data.clientId!!
                    map[Config.client_secret] = data.clientSecret!!
                    authTokenCallBase(map)
                } else getNavigator().showNetworkUnAvailable()
            }
        } else if (apiCode == 2 && response!!.success == true) {
            getNavigator().goToDrawer()
        }
    }

    override fun onFailureApi(call: Call<BaseResponse?>?, t: CustomException?) {
        isLoading.value = false
        isOtpButtonEnabled.set(true)
        if (t!!.code == 1001)
            showConfirmAlert(t.exception!!)
        else getNavigator().showMessage(t.exception!!)

    }

    fun onclickBack() {
        getNavigator().bacpressed()
    }

    fun onClickResend(view:View) {
        isOtpButtonEnabled.set(true)
        startTimer(120000)
        callotpApi()
    }


    fun onClickLogin(view: View) {
        if (otp.get()?.length == 4) {
            isOtpButtonEnabled.set(false)
            getNavigator().setAllowBackNav(false)
            loginOrSignUpApi()
//            Log.e("OTP", getNavigator().getOpt()!!)
        } else
            translationModel?.show_otp_error?.let { getNavigator().showMessage(it) }
    }

    fun loginOrSignUpApi() {
        apiCode = 1
        map.clear()
        map[Config.phone_number] = phoneNumber.get()!!
        map[Config.country_code] = countryCodeId.get()!!
        map[Config.otp] = otp.get().toString()
        map[Config.device_info_hash] = session.getString(SessionMaintainence.FCM_TOKEN)!!
        map[Config.is_primary] = if(phoneNumber.get()!! == "9090909090") "1" else "1"
        loginOrSignUpBase(map)
    }


    fun startTimer(time_in_seconds: Long) {
        enableResend.set(false)
        countdown_timer = object : CountDownTimer(time_in_seconds, 1000) {
            override fun onFinish() {
                enableResend.set(true)
                timerText.set(translationModel?.txt_not_receieve)
                showDidntRecive.set(true)
            }

            override fun onTick(p0: Long) {
                showDidntRecive.set(false)
                time_in_milli_seconds = p0
                updateTextUI()
            }
        }
        countdown_timer.start()

    }

    private fun updateTextUI() {
        val minute = (time_in_milli_seconds / 1000) / 60
        val seconds = (time_in_milli_seconds / 1000) % 60

        timerText.set("${translationModel?.txt_otp_expires} $minute:$seconds")
    }

    fun showConfirmAlert(message: String) {
        val dialog = Dialog(getNavigator().getAct())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.cancel_alert_lay)


        val subHeading = dialog.findViewById<TextView>(R.id.questionText)
        subHeading.setText(message)

        val yes: AppCompatButton = dialog.findViewById(R.id.yesButton)
        yes.text = translationModel?.text_yes

        val no: AppCompatButton = dialog.findViewById(R.id.noButton)
        no.text = translationModel?.text_no

        yes.setOnClickListener {
            if (getNavigator().isNetworkConnected()) {
                // apiCode = 1
                map.clear()
                map[Config.phone_number] = phoneNumber.get()!!
                map[Config.country_code] = countryCodeId.get()!!
                map[Config.otp] = getNavigator().getOpt()!!
                map[Config.device_info_hash] = session.getString(SessionMaintainence.FCM_TOKEN)!!
                map[Config.is_primary] = "1"
                loginOrSignUpBase(map)
                dialog.dismiss()
            } else getNavigator().showNetworkUnAvailable()
        }

        no.setOnClickListener { dialog.dismiss() }



        dialog.show()

    }

    fun updatePhoneInProfile() {
        apiCode = 2
        requestbody.clear()
        requestbody[Config.phone_number] = phoneNumber.get()!!.toRequestBody("text/plain".toMediaTypeOrNull())

        requestbody[Config.otp] = getNavigator().getOpt()!!.toRequestBody("text/plain".toMediaTypeOrNull())
        updateProfileBase()
    }



    fun callotpApi() {
        if (getNavigator().isNetworkConnected()) {
            apiCode = 3
            map.clear()
            map[Config.phone_number] = phoneNumber.get()!!
            map[Config.country_code] = countryCodeId.get()!!
            sendCustomOtp(map)
        } else getNavigator().showNetworkUnAvailable()

    }
    fun onOtpTextChangeListener(e: Editable) {
        if (e.toString().trim().length == 4)
            getNavigator().callHideKeyBoard()
    }


}
